package hyper.reports.database.repository;

import hyper.reports.database.config.HikariCPDataSource;
import hyper.reports.database.repository.specification.QueryInfo;
import hyper.reports.database.repository.specification.Specification;
import hyper.reports.entity.PaymentType;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.RepositoryException;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static hyper.reports.entity.Tables.PAYMENT;

@Slf4j
public class PaymentRepository<T> implements BaseRepository<PaymentType> {

  public PaymentType insert(PaymentType entry) throws RepositoryException, ConnectionException {
    try (Connection connection = HikariCPDataSource.getConnection();
        DSLContext context = DSL.using(connection)) {

      context
          .insertInto(PAYMENT)
          .columns(PAYMENT.TYPE)
          .values(entry.name())
          .onDuplicateKeyIgnore()
          .execute();

      log.info("Insert successful for entry {}", entry);

      return entry;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  public PaymentType update(PaymentType entry) throws RepositoryException, ConnectionException {
    try (Connection connection = HikariCPDataSource.getConnection();
        DSLContext context = DSL.using(connection)) {

      context.update(PAYMENT).set(PAYMENT.TYPE, entry.name()).execute();

      log.info("Update successful for entry {}", entry);

      return entry;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public PaymentType delete(PaymentType entry) throws RepositoryException, ConnectionException {
    try (Connection connection = HikariCPDataSource.getConnection();
        DSLContext context = DSL.using(connection)) {

      context.deleteFrom(PAYMENT).where(PAYMENT.TYPE.eq(entry.name()));

      log.info("Delete successful for entry {}", entry);

      return entry;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public List<PaymentType> query(Specification<PaymentType> specification)
      throws RepositoryException, ConnectionException {
    List<PaymentType> paymentTypes = new ArrayList<>();
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
        PaymentType paymentType;
        if (resultSet.getString(2).equalsIgnoreCase("cash")) {
          paymentType = PaymentType.CASH;
        } else {
          paymentType = PaymentType.CARD;
        }
        paymentTypes.add(paymentType);
      }

      return paymentTypes;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }
}
