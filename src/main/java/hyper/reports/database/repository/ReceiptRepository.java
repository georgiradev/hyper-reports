package hyper.reports.database.repository;

import hyper.reports.database.config.HikariCPDataSource;
import hyper.reports.database.repository.specification.QueryInfo;
import hyper.reports.database.repository.specification.Specification;
import hyper.reports.database.service.card.CardDetailService;
import hyper.reports.database.service.card.CardDetailServiceImpl;
import hyper.reports.entity.CardDetails;
import hyper.reports.entity.PaymentType;
import hyper.reports.entity.Receipt;
import hyper.reports.entity.Store;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.RepositoryException;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static hyper.reports.entity.Tables.RECEIPT;

@Slf4j
public class ReceiptRepository<T> implements BaseRepository<Receipt> {

  private CardDetailService<CardDetails> cardDetailService;

  public ReceiptRepository() {
    this.cardDetailService = new CardDetailServiceImpl();
  }

  public void insertInBatch(Store entry) throws RepositoryException, ConnectionException {
    try (Connection connection = HikariCPDataSource.getConnection();
        PreparedStatement preparedStatement =
            connection.prepareStatement(
                "INSERT INTO receipt(total, date_time, store_id, payment_id, card_id) "
                    + "VALUES(?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {

      for (Receipt receipt : entry.getReceipts()) {
        CardDetails cardDetails = null;
        receipt.setStoreId(entry.getUuid());
        receipt.setPaymentTypeId(receipt.getPayment().name().equalsIgnoreCase("cash") ? 1 : 2);
        try {
          if (receipt.getPaymentTypeId() == 2) {
            cardDetails = cardDetailService.insert(receipt.getCardDetails());
            receipt.setCardDetailsId(cardDetails.getUuid());
          }
          preparedStatement.setDouble(1, receipt.getTotal());
          preparedStatement.setTimestamp(2, Timestamp.valueOf(receipt.getDateTime()));
          preparedStatement.setInt(3, receipt.getStoreId());
          preparedStatement.setInt(4, receipt.getPaymentTypeId());

          if (cardDetails != null && receipt.getPaymentTypeId() == 2) {
            preparedStatement.setInt(5, receipt.getCardDetailsId());
          } else {
            preparedStatement.setNull(5, Types.INTEGER);
          }
          preparedStatement.addBatch();
        } catch (SQLException e) {
          log.error("Failed to insert {}", receipt, e);
          throw new RepositoryException(e);
        }
      }
      preparedStatement.executeBatch();
    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public Receipt update(Receipt entry) throws RepositoryException, ConnectionException {
    try (Connection connection = HikariCPDataSource.getConnection();
        DSLContext context = DSL.using(connection)) {

      context
          .update(RECEIPT)
          .set(
              RECEIPT.CARD_ID,
              entry.getCardDetails() != null ? entry.getCardDetails().getUuid() : null)
          .set(RECEIPT.PAYMENT_ID, entry.getPayment().name().equalsIgnoreCase("cash") ? 1 : 2)
          .set(RECEIPT.STORE_ID, entry.getStoreId())
          .set(RECEIPT.DATE_TIME, entry.getDateTime())
          .set(RECEIPT.TOTAL, entry.getTotal())
          .where(RECEIPT.ID.eq(entry.getUuid()))
          .execute();

      log.info("Update successful for entry {}", entry);

      return entry;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public Receipt delete(Receipt entry) throws RepositoryException, ConnectionException {
    try (Connection connection = HikariCPDataSource.getConnection();
        DSLContext create = DSL.using(connection)) {

      create.deleteFrom(RECEIPT).where(RECEIPT.ID.eq(entry.getUuid())).execute();

      log.info("Delete is successful for entry {}", entry);
      return entry;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public List<Receipt> query(Specification<Receipt> specification)
      throws RepositoryException, ConnectionException {
    List<Receipt> receipts = new ArrayList<>();
    QueryInfo queryInfo = specification.toQueryInfo();
    String sql = queryInfo.getSql();
    Map<Integer, Object> placeholders = queryInfo.getPlaceholders();

    try (Connection connection = HikariCPDataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {

      for (Map.Entry<Integer, Object> entry : placeholders.entrySet()) {
        statement.setObject(entry.getKey(), entry.getValue());
      }
      try (ResultSet resultSet = statement.executeQuery()) {

        while (resultSet.next()) {
          Receipt receipt = new Receipt();
          receipt.setUuid(resultSet.getInt(1));
          receipt.setTotal(resultSet.getDouble(2));
          receipt.setDateTime(resultSet.getTimestamp(3).toLocalDateTime());
          receipt.setStoreId(resultSet.getInt(4));
//          receipt.setPaymentTypeId(resultSet.getInt(5));
//          receipt.setCardDetailsId(resultSet.getInt(6));
          receipt.setPayment(resultSet.getInt(5) == 1 ? PaymentType.CASH : PaymentType.CARD);
          int cardId = resultSet.getInt(6);
          if (cardId == 0) {
            receipt.setCardDetails(null);
          } else {
            Optional<CardDetails> cardDetailsOptional = cardDetailService.findByID(cardId);
            if (cardDetailsOptional.isPresent()) {
              receipt.setCardDetails(cardDetailsOptional.get());
            }
          }
          receipts.add(receipt);
        }
      }

      return receipts;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }
}
