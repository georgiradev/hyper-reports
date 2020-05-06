package hyper.reports.database.repository;

import hyper.reports.database.config.HikariCPDataSource;
import hyper.reports.database.repository.specification.QueryInfo;
import hyper.reports.database.repository.specification.Specification;
import hyper.reports.entity.Customer;
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

import static hyper.reports.entity.Tables.CUSTOMER;

@Slf4j
public class CustomerRepository<T> implements BaseRepository<Customer> {

  public Customer insert(Customer entry) throws ConnectionException, RepositoryException {
    try (Connection connection = HikariCPDataSource.getConnection();
        DSLContext context = DSL.using(connection)) {

      context
          .insertInto(CUSTOMER)
          .columns(CUSTOMER.ID, CUSTOMER.NAME, CUSTOMER.ADDRESS)
          .values(entry.getUuid(), entry.getName(), entry.getAddress())
          .onConflictDoNothing()
          .execute();

      log.info("Insert successful for entry {}", entry);

      return entry;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public Customer update(Customer entry) throws RepositoryException, ConnectionException {
    try (Connection connection = HikariCPDataSource.getConnection();
        DSLContext context = DSL.using(connection)) {

      context
          .update(CUSTOMER)
          .set(CUSTOMER.NAME, entry.getName())
          .set(CUSTOMER.ADDRESS, entry.getAddress())
          .where(CUSTOMER.ID.eq(entry.getUuid()))
          .execute();

      log.info("Update successful for entry {}", entry);

      return entry;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public Customer delete(Customer entry) throws RepositoryException, ConnectionException {
    try (Connection connection = HikariCPDataSource.getConnection();
        DSLContext context = DSL.using(connection)) {

      context.deleteFrom(CUSTOMER).where(CUSTOMER.ID.eq(entry.getUuid())).execute();

      log.info("Delete is successful for entry {}", entry);

      return entry;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public List<Customer> query(Specification<Customer> specification)
      throws RepositoryException, ConnectionException {
    List<Customer> customers = new ArrayList<>();
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
        Customer customer = new Customer();
        int id = resultSet.getInt(1);
        String name = resultSet.getString(2);
        String address = resultSet.getString(3);
        customer.setUuid(id);
        customer.setName(name);
        customer.setAddress(address);
        customers.add(customer);
      }

      return customers;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }
}
