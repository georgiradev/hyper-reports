package hyper.reports.database.repository;

import hyper.reports.database.config.HikariCPDataSource;
import hyper.reports.database.repository.specification.QueryInfo;
import hyper.reports.database.repository.specification.Specification;
import hyper.reports.database.service.card.CardDetailService;
import hyper.reports.database.service.card.CardDetailServiceImpl;
import hyper.reports.database.service.customer.CustomerService;
import hyper.reports.database.service.customer.CustomerServiceImpl;
import hyper.reports.entity.*;
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

import static hyper.reports.entity.Tables.INVOICE;

@Slf4j
public class InvoiceRepository<T> implements BaseRepository<Invoice> {

  private CardDetailService<CardDetails> cardDetailService;
  private CustomerService<Customer> customerService;

  public InvoiceRepository() {
    this.cardDetailService = new CardDetailServiceImpl();
    this.customerService = new CustomerServiceImpl();
  }

  public void insertBatch(Store entry) throws RepositoryException, ConnectionException {
    try (Connection connection = HikariCPDataSource.getConnection();
        PreparedStatement preparedStatement =
            connection.prepareStatement(
                "INSERT INTO invoice(total, date_time, store_id, customer_id, payment_id, card_id) "
                    + "VALUES(?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {

      for (Invoice invoice : entry.getInvoices()) {
        CardDetails cardDetails = null;
        invoice.setStoreId(entry.getUuid());
        invoice.setCustomerId(invoice.getCustomer().getUuid());
        invoice.setPaymentTypeId(invoice.getPayment().name().equalsIgnoreCase("cash") ? 1 : 2);
        try {
          if (invoice.getPaymentTypeId() == 2) {
            cardDetails = cardDetailService.insert(invoice.getCardDetails());
            invoice.setCardDetailsId(cardDetails.getUuid());
          }
          customerService.insert(invoice.getCustomer());
          preparedStatement.setDouble(1, invoice.getTotal());
          preparedStatement.setTimestamp(2, Timestamp.valueOf(invoice.getDateTime()));
          preparedStatement.setInt(3, invoice.getStoreId());
          preparedStatement.setInt(4, invoice.getCustomerId());
          preparedStatement.setInt(5, invoice.getPaymentTypeId());
          if (cardDetails != null && invoice.getPaymentTypeId() == 2) {
            preparedStatement.setInt(6, invoice.getCardDetailsId());
          } else {
            preparedStatement.setNull(6, Types.INTEGER);
          }
          preparedStatement.addBatch();
        } catch (SQLException e) {
          log.error("Failed to insert {}", invoice, e);
          throw new RepositoryException(e);
        }
      }
      preparedStatement.executeBatch();
    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public Invoice update(Invoice entry) throws RepositoryException, ConnectionException {
    try (Connection connection = HikariCPDataSource.getConnection();
        DSLContext context = DSL.using(connection)) {

      context
          .update(INVOICE)
          .set(
              INVOICE.CARD_ID,
              entry.getCardDetails() != null ? entry.getCardDetails().getUuid() : null)
          .set(INVOICE.PAYMENT_ID, entry.getPayment().name().equalsIgnoreCase("cash") ? 1 : 2)
          .set(INVOICE.CUSTOMER_ID, entry.getCustomer().getUuid())
          .set(INVOICE.DATE_TIME, entry.getDateTime())
          .set(INVOICE.TOTAL, entry.getTotal())
          .where(INVOICE.ID.eq(entry.getUuid()))
          .execute();

      log.info("Update successful for entry {}", entry);

      return entry;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public Invoice delete(Invoice entry) throws RepositoryException, ConnectionException {
    try (Connection connection = HikariCPDataSource.getConnection();
        DSLContext create = DSL.using(connection)) {

      create.deleteFrom(INVOICE).where(INVOICE.ID.eq(entry.getUuid())).execute();

      log.info("Delete is successful for entry {}", entry);

      return entry;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public List<Invoice> query(Specification<Invoice> specification)
      throws RepositoryException, ConnectionException {
    List<Invoice> invoices = new ArrayList<>();
    QueryInfo queryInfo = specification.toQueryInfo();
    String sql = queryInfo.getSql();
    Map<Integer, Object> placeholders = queryInfo.getPlaceholders();

    try (Connection connection = HikariCPDataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {

      for (Map.Entry<Integer, Object> entry : placeholders.entrySet()) {
        statement.setObject(entry.getKey(), entry.getValue());
      }
      ResultSet resultSet = statement.executeQuery();

      while (resultSet.next()) {
        Invoice invoice = new Invoice();
        invoice.setUuid(resultSet.getInt(1));
        invoice.setTotal(resultSet.getDouble(2));
        invoice.setDateTime(resultSet.getTimestamp(3).toLocalDateTime());
        invoice.setStoreId(resultSet.getInt(4));
        invoice.setCustomerId(resultSet.getInt(5));
        invoice.setPayment(resultSet.getInt(6) == 1 ? PaymentType.CASH : PaymentType.CARD);
        int cardId = resultSet.getInt(7);
        if (cardId == 0) {
          invoice.setCardDetails(null);
        } else {
          Optional<CardDetails> cardDetailsOptional = cardDetailService.findByID(cardId);
          if (cardDetailsOptional.isPresent()) {
            invoice.setCardDetails(cardDetailsOptional.get());
          }
        }
        invoices.add(invoice);
      }

      return invoices;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }
}
