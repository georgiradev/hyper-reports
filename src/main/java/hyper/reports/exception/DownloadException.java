package hyper.reports.exception;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class DownloadException extends Exception {
  public DownloadException(JSchException e) {
    super(e);
  }

  public DownloadException(SftpException e) {
    super(e);
  }

  public DownloadException(Exception e) {
    super(e);
  }
}
