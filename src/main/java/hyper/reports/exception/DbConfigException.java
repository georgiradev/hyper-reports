package hyper.reports.exception;

import java.io.IOException;

public class DbConfigException extends ConnectionException {
  public DbConfigException(IOException e) {
    super(e);
  }

  public DbConfigException(DbConfigException e) {
    super(e);
  }
}
