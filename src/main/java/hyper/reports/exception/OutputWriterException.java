package hyper.reports.exception;

import java.io.IOException;

public class OutputWriterException extends Exception {
  public OutputWriterException(IOException e) {
    super(e);
  }
}
