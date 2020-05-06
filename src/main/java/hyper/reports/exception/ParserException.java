package hyper.reports.exception;

import java.io.IOException;

public class ParserException extends Exception {
  public ParserException(IOException e) {
    super(e);
  }
}
