package hyper.reports.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import hyper.reports.database.service.company.CompanyService;
import hyper.reports.database.service.company.CompanyServiceImpl;
import hyper.reports.download.SFTPDownloader;
import hyper.reports.entity.Company;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.DownloadException;
import hyper.reports.exception.ParserException;
import hyper.reports.exception.RepositoryException;
import hyper.reports.report.Report;
import hyper.reports.report.ReportEngine;
import hyper.reports.report.parser.XlsxParser;
import hyper.reports.scanner.XmlScanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class MainCLI {

  private final HyperReportsCommands parameters = new HyperReportsCommands();
  private CompanyService<Company> companyService = new CompanyServiceImpl();
  private String exportDirPath;
  private String localDirPath;
  private List<Company> companies;

  public static void main(String[] args)
      throws IOException, ParserException, DownloadException, ConnectionException,
          RepositoryException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    MainCLI cli = new MainCLI();

    while (true) {
      System.out.println();
      System.out.println("Enter command");
      String[] input = reader.readLine().trim().split("\\s+");
      cli.handleInputArgs(input);
    }
  }

  public void handleInputArgs(String[] args)
      throws DownloadException, ParserException, ConnectionException, RepositoryException {

    JCommander jCommander =
        JCommander.newBuilder()
            .programName("hyper-reports")
            .addCommand("hyper-reports", parameters)
            .build();

    try {
      jCommander.parse(args);
    } catch (ParameterException e) {
      System.err.println(e.getMessage());
      return;
    }

    if (parameters.isHelp()) {
      showUsage(jCommander);
      parameters.setHelp(false);

    } else if (parameters.isConfig()) {
      if (parameters.getPathDataDir() != null) {
        parameters.setPathDataDir(validatePath(parameters.getPathDataDir()));
        this.localDirPath = parameters.getPathDataDir();
      }
      if (parameters.getPathExportDir() != null) {
        parameters.setPathExportDir(validatePath(parameters.getPathExportDir()));
        this.exportDirPath = parameters.getPathExportDir();
      }
      parameters.setConfig(false);

    } else if (parameters.isProcess()) {
      if (localDirPath != null) {
        deserialize();
      } else {
        System.err.println("There is no local path specified");
      }
      parameters.setProcess(false);

    } else if (parameters.isDownload()) {
      if (localDirPath != null) {
        download();
      } else {
        System.err.println("There is no local path specified");
      }
      parameters.setDownload(false);

    } else if (parameters.isReport()) {
      if (validate(parameters)) {
        exportParsedData();
      }
      parameters.setReport(false);

    } else if (parameters.isExit()) {
      System.out.println("Goodbye");
      System.exit(0);
    } else if (parameters.isInsert()) {
      if (this.companies != null && !this.companies.isEmpty()) {
        insert();
      } else {
        System.err.println("Xml files not deserialized. You must first run process command.");
      }
      parameters.setInsert(false);
    }
  }

  private void insert() throws ConnectionException, RepositoryException {
    for (Company company : this.companies) {
      companyService.insert(company);
    }
  }

  private void exportParsedData() throws ConnectionException, RepositoryException {
    ReportEngine engine = new ReportEngine();
    Company company = companyService.findByName(parameters.getCompanyName());

    if (company == null) {
      System.err.println("Company not found");
      return;
    }

    XlsxParser parser = new XlsxParser();
    Report report;

    switch (parameters.getAggregation().toLowerCase()) {
      case "store":
        if (parameters.getQuarter() != null) {
          report =
              engine.createStoreQuarterReport(
                  parameters.getYear(),
                  parameters.getQuarter(),
                  company.getUuid(),
                  parameters.getOrder());
        } else if (parameters.getMonth() != 0) {
          report =
              engine.createStoreMonthReport(
                  parameters.getYear(),
                  parameters.getMonth(),
                  company.getUuid(),
                  parameters.getOrder());
        } else {
          report =
              engine.createStoreYearReport(
                  parameters.getYear(), company.getUuid(), parameters.getOrder());
        }
        break;

      case "invoice":
        if (parameters.getQuarter() != null) {
          report =
              engine.createInvoiceQuarterReport(
                  parameters.getYear(), parameters.getQuarter(), company.getUuid());

        } else if (parameters.getMonth() != 0) {
          report =
              engine.createInvoiceMonthReport(
                  parameters.getYear(), parameters.getMonth(), company.getUuid());
        } else {
          report = engine.createInvoiceYearReport(parameters.getYear(), company.getUuid());
        }
        break;

      case "receipt":
        if (parameters.getQuarter() != null) {
          report =
              engine.createReceiptQuarterReport(
                  parameters.getYear(), parameters.getQuarter(), company.getUuid());
        } else if (parameters.getMonth() != 0) {
          report =
              engine.createReceiptMonthReport(
                  parameters.getYear(), parameters.getMonth(), company.getUuid());
        } else {
          report = engine.createReceiptYearReport(parameters.getYear(), company.getUuid());
        }
        break;

      case "payment":
        report = engine.createPaymentYearReport(parameters.getYear(), company.getUuid());
        break;

      default:
        throw new IllegalStateException("Unexpected value: " + parameters.getAggregation());
    }

    parser.exportAsXlsx(report, exportDirPath, parameters);
    parameters.resetAggregations();
  }

  private String validatePath(String path) {
    if (!path.substring(path.length() - 1).equals("/")) {
      path = path + "/";
    }
    return path;
  }

  private boolean validate(HyperReportsCommands parameters) {
    if (exportDirPath == null) {
      System.err.println("There is no export path specified");
      return false;
    }

    exportDirPath = validatePath(exportDirPath);

    if (parameters.getCompanyName() == null) {
      System.err.println("Company name required");
      return false;
    }
    if (parameters.getYear() == 0) {
      System.err.println("Year is required");
      return false;
    }
    if (parameters.getAggregation() == null) {
      System.err.println("Aggregation field is required");
      return false;
    }
    return true;
  }

  private void download() throws DownloadException {
    SFTPDownloader downloader = new SFTPDownloader();
    downloader.downloadAll(localDirPath);
  }

  private void deserialize() throws ParserException {
    XmlScanner xmlScanner = new XmlScanner();
    this.companies = xmlScanner.getResourceFiles(localDirPath);
  }

  public void showUsage(JCommander jCommander) {
    jCommander.usage();
  }
}
