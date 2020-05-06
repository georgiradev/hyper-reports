package hyper.reports;

import hyper.reports.database.config.FlyWayMigration;
import hyper.reports.database.service.company.CompanyService;
import hyper.reports.database.service.company.CompanyServiceImpl;
import hyper.reports.download.SFTPDownloader;
import hyper.reports.download.config.SFTPConfig;
import hyper.reports.entity.Company;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.DownloadException;
import hyper.reports.exception.ParserException;
import hyper.reports.exception.RepositoryException;
import hyper.reports.scanner.XmlScanner;
import org.flywaydb.core.Flyway;

import java.util.List;

public class Main {

  public static void main(String[] args)
      throws RepositoryException, DownloadException, ParserException, ConnectionException {

    // OutputWriter.writeAsCsvFile(company, "company.csv");

    SFTPDownloader downloader = new SFTPDownloader();
    downloader.downloadAll(new SFTPConfig().getLocalDir());

    XmlScanner xmlScanner = new XmlScanner();
    List<Company> companies = xmlScanner.getResourceFiles(new SFTPConfig().getLocalDir());

    Flyway migration = FlyWayMigration.getInstance();
    migration.migrate();

    CompanyService<Company> companyService = new CompanyServiceImpl();
    for (Company company : companies) {
      companyService.insert(company);
    }
  }
}
