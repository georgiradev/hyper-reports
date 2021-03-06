package hyper.reports.database.repository.specification.invoice;

import hyper.reports.database.config.DBConfig;
import hyper.reports.database.config.HikariCPDataSource;
import hyper.reports.database.repository.specification.QueryInfo;
import hyper.reports.database.repository.specification.Specification;
import hyper.reports.entity.Invoice;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.DbConfigException;
import hyper.reports.exception.RepositoryException;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static hyper.reports.entity.Tables.INVOICE;
import static org.jooq.impl.DSL.month;
import static org.jooq.impl.DSL.year;

public class FindInvoicesByStoreAndMonth implements Specification<Invoice> {

  private int storeId;
  private int month;
  private int year;

  public FindInvoicesByStoreAndMonth(int storeId, int month, int year) {
    this.storeId = storeId;
    this.month = month;
    this.year = year;
  }

  @Override
  public QueryInfo toQueryInfo() throws RepositoryException, ConnectionException {
    QueryInfo info = new QueryInfo();
    try (Connection connection = HikariCPDataSource.getConnection();
        DSLContext create = DSL.using(connection, new DBConfig().getDatabaseDialect())) {
      // builder can be used here
      String sql =
          create
              .selectFrom(INVOICE)
              .where(INVOICE.STORE_ID.eq(storeId))
              .and(month(INVOICE.DATE_TIME).eq(month))
              .and(year(INVOICE.DATE_TIME).eq(year))
              .getSQL();
      Map<Integer, Object> placeholders = new HashMap<>();
      placeholders.put(1, storeId);
      placeholders.put(2, month);
      placeholders.put(3, year);
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
