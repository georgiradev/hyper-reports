package hyper.reports.database.repository;

import hyper.reports.database.config.HikariCPDataSource;
import hyper.reports.database.repository.specification.QueryInfo;
import hyper.reports.database.repository.specification.Specification;
import hyper.reports.entity.CardDetails;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.RepositoryException;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static hyper.reports.entity.Tables.CARD;

@Slf4j
public class CardDetailsRepository<T> implements BaseRepository<CardDetails> {

  public CardDetails insert(CardDetails entry) throws RepositoryException, ConnectionException {
    try (Connection connection = HikariCPDataSource.getConnection();
        DSLContext create = DSL.using(connection)) {

      Record1<Integer> record =
          create
              .insertInto(CARD)
              .columns(CARD.CARD_TYPE, CARD.NUMBER, CARD.CONTACTLESS)
              .values(
                  entry.getCardType(),
                  String.valueOf(entry.getNumber()),
                  String.valueOf(entry.isContactLess()))
              .returningResult(CARD.ID)
              .fetchOne();

      entry.setUuid(record.getValue(CARD.ID));
      log.info("Insert successful for entry {}", entry);

      return entry;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public CardDetails update(CardDetails entry) throws RepositoryException, ConnectionException {
    try (Connection connection = HikariCPDataSource.getConnection();
        DSLContext create = DSL.using(connection)) {

      Record1<Integer> record =
          create
              .update(CARD)
              .set(CARD.CARD_TYPE, entry.getCardType())
              .set(CARD.NUMBER, String.valueOf(entry.getNumber()))
              .set(CARD.CONTACTLESS, String.valueOf(entry.isContactLess()))
              .where(CARD.ID.eq(entry.getUuid()))
              .returningResult(CARD.ID)
              .fetchOne();

      log.info("Update successful for entry {}", entry);

      return entry;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public CardDetails delete(CardDetails entry) throws RepositoryException, ConnectionException {
    try (Connection connection = HikariCPDataSource.getConnection();
        DSLContext create = DSL.using(connection)) {

      create.deleteFrom(CARD).where(CARD.ID.eq(entry.getUuid())).execute();

      log.info("Delete successful for entry {}", entry);

      return entry;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public List<CardDetails> query(Specification<CardDetails> specification)
      throws RepositoryException, ConnectionException {
    List<CardDetails> list = new ArrayList<>();
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
          CardDetails cardDetails = new CardDetails();
          int id = resultSet.getInt(1);
          String cardType = resultSet.getString(2);
          String number = resultSet.getString(3);
          String contactLess = resultSet.getString(4);
          cardDetails.setUuid(id);
          cardDetails.setCardType(cardType);
          cardDetails.setNumber(number);
          cardDetails.setContactLess(Boolean.parseBoolean(contactLess));
          list.add(cardDetails);
        }
      }

      return list;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }
}
