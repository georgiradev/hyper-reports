package hyper.reports.database.config;

import hyper.reports.exception.ConnectionException;
import org.flywaydb.core.Flyway;

public final class FlyWayMigration {

  private FlyWayMigration() {}

  public static Flyway getInstance() throws ConnectionException {
    DBConfig dbConfig = new DBConfig();
    return Flyway.configure()
        .dataSource(dbConfig.getJDBCUrl(), dbConfig.getJDBCUser(), dbConfig.getJDBCPassword())
        .load();
  }
}
