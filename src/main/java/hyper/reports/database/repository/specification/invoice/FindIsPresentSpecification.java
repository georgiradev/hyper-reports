package hyper.reports.database.repository.specification.invoice;

import hyper.reports.database.config.DBConfig;
import hyper.reports.database.config.HikariCPDataSource;
import hyper.reports.database.repository.specification.QueryInfo;
import hyper.reports.database.repository.specification.Specification;
import hyper.reports.entity.Invoice;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.DbConfigException;
import hyper.reports.exception.RepositoryException;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static hyper.reports.entity.Tables.INVOICE;

@AllArgsConstructor
public class FindIsPresentSpecification implements Specification<Invoice> {

  private double total;
  private int storeId;
  private LocalDateTime dateTime;
  private int paymentId;
  private int customerId;

  @Override
  public QueryInfo toQueryInfo() throws RepositoryException, ConnectionException {
    QueryInfo info = new QueryInfo();
    try (Connection connection = HikariCPDataSource.getConnection();
        DSLContext create = DSL.using(connection, new DBConfig().getDatabaseDialect())) {
      // builder can be used here
      String sql =
          create
              .selectFrom(INVOICE)
              .where(INVOICE.TOTAL.eq(total))
              .and(INVOICE.DATE_TIME.eq(dateTime))
              .and(INVOICE.STORE_ID.eq(storeId))
              .and(INVOICE.CUSTOMER_ID.eq(customerId))
              .and(INVOICE.PAYMENT_ID.eq(paymentId))
              .getSQL();
      Map<Integer, Object> placeholders = new HashMap<>();
      placeholders.put(1, total);
      placeholders.put(2, dateTime);
      placeholders.put(3, storeId);
      placeholders.put(4, customerId);
      placeholders.put(5, paymentId);
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
