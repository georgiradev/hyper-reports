package hyper.reports.database.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hyper.reports.exception.ConnectionException;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public final class HikariCPDataSource {

  private static final HikariConfig config = new HikariConfig();
  private static HikariDataSource ds;

  static {
    try {
      DBConfig dbConfig = new DBConfig();
      config.setJdbcUrl(dbConfig.getJDBCUrl());
      config.setUsername(dbConfig.getJDBCUser());
      config.setPassword(dbConfig.getJDBCPassword());
      config.addDataSourceProperty("cachePrepStmts", "true");
      config.addDataSourceProperty("prepStmtCacheSize", "250");
      config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
      ds = new HikariDataSource(config);
    } catch (ConnectionException e) {
      log.error("DBConnection Error", e);
    }
  }

  public static Connection getConnection() throws ConnectionException {
    try {
      return ds.getConnection();
    } catch (SQLException e) {
      throw new ConnectionException(e);
    }
  }

  private HikariCPDataSource() {}
}
