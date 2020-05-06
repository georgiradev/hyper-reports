package hyper.reports.exception;

import java.io.IOException;
import java.sql.SQLException;

public class ConnectionException extends Exception {
  public ConnectionException(SQLException ex) {
    super(ex);
  }

  public ConnectionException(IOException e) {
    super(e);
  }

  public ConnectionException(DbConfigException e) {
    super(e);
  }

  public ConnectionException(ConnectionException e) {
    super(e);
  }
}
