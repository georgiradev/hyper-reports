package hyper.reports.download.config;

import hyper.reports.exception.DownloadException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class SFTPConfig {

  private Properties info = new Properties();
  private InputStream input;

  public SFTPConfig() throws DownloadException {
    try {
      input =
          SFTPConfig.class
              .getClassLoader()
              .getResourceAsStream("sftp-downloader-config.properties");
      info.load(input);
    } catch (IOException ex) {
      log.error("SFTP load failure ", ex);
      throw new DownloadException(ex);
    }
  }

  public String getRemoteHost() {
    return info.getProperty("remote.host");
  }

  public String getUserName() {
    return info.getProperty("username");
  }

  public String getPassword() {
    return info.getProperty("password");
  }

  public int getPort() {
    return Integer.parseInt(info.getProperty("port"));
  }

  public String getHostURI() {
    return info.getProperty("host.uri");
  }

  public String getHostFolder() {
    return info.getProperty("host.folder");
  }

  public String getLocalDir() {
    return info.getProperty("local.dir");
  }
}
