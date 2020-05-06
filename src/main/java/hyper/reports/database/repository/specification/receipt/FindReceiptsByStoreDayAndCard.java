package hyper.reports.database.repository.specification.receipt;

import hyper.reports.database.config.DBConfig;
import hyper.reports.database.config.HikariCPDataSource;
import hyper.reports.database.repository.specification.QueryInfo;
import hyper.reports.database.repository.specification.Specification;
import hyper.reports.entity.Receipt;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.DbConfigException;
import hyper.reports.exception.RepositoryException;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static hyper.reports.entity.Tables.*;
import static org.jooq.impl.DSL.month;
import static org.jooq.impl.DSL.year;

public class FindReceiptsByStoreDayAndCard implements Specification<Receipt> {

  private int companyId;
  private int month;
  private int year;
  private int paymentTypeId;

  public FindReceiptsByStoreDayAndCard(int companyId, int month, int year, int paymentTypeId) {
    this.companyId = companyId;
    this.month = month;
    this.year = year;
    this.paymentTypeId = paymentTypeId;
  }

  @Override
  public QueryInfo toQueryInfo() throws RepositoryException, ConnectionException {
    QueryInfo info = new QueryInfo();
    try (Connection connection = HikariCPDataSource.getConnection();
        DSLContext create = DSL.using(connection, new DBConfig().getDatabaseDialect())) {
      // builder can be used here
      String sql =
          create
              .select()
              .from(RECEIPT)
              .innerJoin(STORE)
              .on(STORE.ID.eq(RECEIPT.STORE_ID))
              .innerJoin(COMPANY)
              .on(COMPANY.ID.eq(STORE.COMPANY_ID))
              .where(COMPANY.ID.eq(companyId))
              .and(month(RECEIPT.DATE_TIME).eq(month))
              .and(year(RECEIPT.DATE_TIME).eq(year))
              .and(RECEIPT.PAYMENT_ID.eq(paymentTypeId))
              .getSQL();

      Map<Integer, Object> placeholders = new HashMap<>();
      placeholders.put(1, companyId);
      placeholders.put(2, month);
      placeholders.put(3, year);
      placeholders.put(4, paymentTypeId);
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
