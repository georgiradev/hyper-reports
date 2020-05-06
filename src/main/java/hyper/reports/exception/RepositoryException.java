package hyper.reports.exception;

import java.io.IOException;
import java.sql.SQLException;

public class RepositoryException extends Exception {
  public RepositoryException(SQLException e) {
    super(e);
  }

  public RepositoryException(String message) {
    super(message);
  }

  public RepositoryException(String message, SQLException e) {
    super(message, e);
  }

  public RepositoryException(IOException e) {
    super();
  }

  public RepositoryException(RepositoryException e) {
    super();
  }
}
