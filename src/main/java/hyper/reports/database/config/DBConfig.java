package hyper.reports.database.config;

import hyper.reports.exception.ConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.jooq.SQLDialect;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class DBConfig {

  private Properties info = new Properties();
  private InputStream input;

  public DBConfig() throws ConnectionException {
    try {
      input = DBConfig.class.getClassLoader().getResourceAsStream("dbconfig.properties");
      info.load(input);
    } catch (IOException ex) {
      throw new ConnectionException(ex);
    }
  }

  public String getJDBCDriver() {
    return info.getProperty("jdbc.driver");
  }

  public String getJDBCUrl() {
    return info.getProperty("jdbc.url");
  }

  public String getJDBCUser() {
    return info.getProperty("jdbc.user");
  }

  public String getJDBCPassword() {
    return info.getProperty("jdbc.password");
  }

  public SQLDialect getDatabaseDialect() {
    return SQLDialect.valueOf(info.getProperty("database.dialect"));
  }
}
