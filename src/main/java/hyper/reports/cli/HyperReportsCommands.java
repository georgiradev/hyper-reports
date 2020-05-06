package hyper.reports.cli;

import com.beust.jcommander.Parameter;
import hyper.reports.cli.spliter.EmptyStringSplitter;
import hyper.reports.cli.validator.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class HyperReportsCommands {

  @Parameter(
      names = {"--help", "-h", "help"},
      help = true,
      description = "Displays help information")
  private boolean help;

  @Parameter(
      names = {"config", "--config"},
      description = "Configure the path")
  private boolean config;

  @Parameter(
      names = {"--process", "process", "-p"},
      description = "Deserialize all new data")
  private boolean process;

  @Parameter(
      names = {"--report", "report"},
      description = "Executing a report. The result of each report is an .xlsx file")
  private boolean report;

  @Parameter(
      names = {"--download", "download", "-d"},
      description = "Download the files from sftp server")
  private boolean download;

  @Parameter(
      names = {"--data-dir", "-dd"},
      validateWith = DirectoryValidator.class,
      description = "A directory where the data files are stored locally")
  private String pathDataDir;

  @Parameter(
      names = {"--export-dir", "-ed"},
      validateWith = DirectoryValidator.class,
      description = "A directory in which the exported xlsx files will be stored")
  private String pathExportDir;

  @Parameter(names = {"--company", "-c"})
  private String companyName;

  @Parameter(
      names = {"--month", "-m"},
      validateWith = MonthValidator.class)
  private int month;

  @Parameter(
      names = {"--year", "-y"},
      validateWith = YearValidator.class)
  private int year;

  @Parameter(
      names = {"--quarter", "-q"},
      splitter = EmptyStringSplitter.class,
      validateWith = QuarterValidator.class)
  private String quarter;

  @Parameter(
      names = {"--aggregation", "-a"},
      validateWith = AggregationValidator.class)
  private String aggregation;

  @Parameter(names = {"--top", "-t"})
  private int top;

  @Parameter(
      names = {"--order", "-o"},
      validateWith = OrderValidator.class)
  private String order;

  @Parameter(names = {"exit"})
  private boolean exit;

  @Parameter(names = {"insert", "--insert", "-i"})
  private boolean insert;

  public void resetAggregations() {
    order = null;
    top = 0;
    quarter = null;
    year = 0;
    month = 0;
    companyName = null;
    aggregation = null;
  }
}
