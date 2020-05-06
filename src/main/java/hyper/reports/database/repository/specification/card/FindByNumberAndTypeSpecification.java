package hyper.reports.database.repository.specification.card;

import hyper.reports.database.config.DBConfig;
import hyper.reports.database.config.HikariCPDataSource;
import hyper.reports.database.repository.specification.QueryInfo;
import hyper.reports.database.repository.specification.Specification;
import hyper.reports.entity.CardDetails;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.RepositoryException;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static hyper.reports.entity.Tables.CARD;

@AllArgsConstructor
public class FindByNumberAndTypeSpecification implements Specification<CardDetails> {

  private String number;
  private String cardType;
  private String contactLess;

  @Override
  public QueryInfo toQueryInfo() throws RepositoryException, ConnectionException {
    QueryInfo info = new QueryInfo();
    try (Connection connection = HikariCPDataSource.getConnection();
        DSLContext create = DSL.using(connection, new DBConfig().getDatabaseDialect())) {
      // builder can be used here
      String sql =
          create
              .selectFrom(CARD)
              .where(CARD.NUMBER.eq(number))
              .and(CARD.CARD_TYPE.eq(cardType))
              .and(CARD.CONTACTLESS.eq(contactLess))
              .getSQL();
      Map<Integer, Object> placeholders = new HashMap<>();
      placeholders.put(1, number);
      placeholders.put(2, cardType);
      placeholders.put(3, contactLess);
      info.setSql(sql);
      info.setPlaceholders(placeholders);
    } catch (SQLException e) {
      throw new RepositoryException(e);
    } catch (ConnectionException e) {
      throw new ConnectionException(e);
    }
    return info;
  }
}
