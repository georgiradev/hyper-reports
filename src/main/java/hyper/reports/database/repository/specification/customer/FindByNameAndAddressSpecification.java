package hyper.reports.database.repository.specification.customer;

import hyper.reports.database.config.DBConfig;
import hyper.reports.database.config.HikariCPDataSource;
import hyper.reports.database.repository.specification.QueryInfo;
import hyper.reports.database.repository.specification.Specification;
import hyper.reports.entity.Customer;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.DbConfigException;
import hyper.reports.exception.RepositoryException;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static hyper.reports.entity.Tables.CUSTOMER;

@AllArgsConstructor
public class FindByNameAndAddressSpecification implements Specification<Customer> {

  private int id;
  private String name;
  private String address;

  @Override
  public QueryInfo toQueryInfo() throws RepositoryException, ConnectionException {
    QueryInfo info = new QueryInfo();
    try (Connection connection = HikariCPDataSource.getConnection();
        DSLContext create = DSL.using(connection, new DBConfig().getDatabaseDialect())) {
      // builder can be used here
      String sql =
          create
              .selectFrom(CUSTOMER)
              .where(CUSTOMER.ID.eq(id))
              .and(CUSTOMER.NAME.eq(name))
              .and(CUSTOMER.ADDRESS.eq(address))
              .getSQL();
      Map<Integer, Object> placeholders = new HashMap<>();
      placeholders.put(1, id);
      placeholders.put(2, name);
      placeholders.put(3, address);
      info.setSql(sql);
      info.setPlaceholders(placeholders);
    } catch (SQLException e) {
      throw new RepositoryException(e);
    } catch (DbConfigException e) {
      throw new ConnectionException(e);
    }
    return info;
  }
}
