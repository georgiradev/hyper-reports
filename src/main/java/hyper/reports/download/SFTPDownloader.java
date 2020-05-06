package hyper.reports.download;

import com.jcraft.jsch.*;
import hyper.reports.download.config.SFTPConfig;
import hyper.reports.exception.DownloadException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class SFTPDownloader {

  private Session session;
  private ChannelSftp sftp;
  private final SFTPConfig sftpConfig = new SFTPConfig();

  public SFTPDownloader() throws DownloadException {}

  private ChannelSftp setupJsch() throws DownloadException {
    JSch jsch = new JSch();
    java.util.Properties config = new java.util.Properties();
    config.put("StrictHostKeyChecking", "no");
    try {
      jsch.setKnownHosts(sftpConfig.getHostURI());
      session =
          jsch.getSession(
              sftpConfig.getUserName(), sftpConfig.getRemoteHost(), sftpConfig.getPort());
      session.setConfig(config);
      session.setPassword(sftpConfig.getPassword());
      session.connect();
      log.info("Connection successful");
      return (ChannelSftp) session.openChannel("sftp");
    } catch (JSchException e) {
      throw new DownloadException(e);
    }
  }

  private void download(String fileName, String path) throws DownloadException {
    try {
      sftp.get(sftpConfig.getHostFolder() + fileName, path);
      log.info(
          "{} is downloaded in directory: {} on {}",
          fileName,
          path,
          LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    } catch (SftpException e) {
      throw new DownloadException(e);
    }
  }

  public void downloadAll(String path) throws DownloadException {
    this.sftp = setupJsch();

    checkIfLocalDirExist(path);

    log.info("Downloading...");
    int count = 0;
    try {
      sftp.connect();
      List<ChannelSftp.LsEntry> list = sftp.ls(sftpConfig.getHostFolder() + "*.xml");
      Set<ChannelSftp.LsEntry> uniqueValues = new HashSet<>();

      for (ChannelSftp.LsEntry lsEntry : list) {
        String fileName = lsEntry.getFilename();
        if (!isFileExist(fileName, path)) {
          if (uniqueValues.add(lsEntry)) {
            download(lsEntry.getFilename(), path);
          }
          count++;
        }
      }

      if (count > 0) {
        log.info("Download complete! {} files have been downloaded.", count);
      } else {
        log.info("All files are already downloaded");
      }
    } catch (JSchException | SftpException e) {
      throw new DownloadException(e);
    } finally {
      destroy();
    }
  }

  private void checkIfLocalDirExist(String path) {
    File headerFolder = new File(path);

    if (!headerFolder.exists()) {
      headerFolder.mkdirs();
    }
  }

  private boolean isFileExist(String filename, String path) {
    if (!path.substring(path.length() - 1).equals("/")) {
      path = path + "/";
    }
    String pathName = path + filename;
    File tempFile = new File(pathName);
    return tempFile.exists();
  }

  private void destroy() {
    sftp.exit();
    sftp.disconnect();
    log.info("ChanelSftp shutdown");
    session.disconnect();
    log.info("Session is closed");
  }
}
