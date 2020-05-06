package hyper.reports.output;

import hyper.reports.entity.Company;
import hyper.reports.exception.OutputWriterException;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
public class OutputWriter {

  private OutputWriter() {}

  public static void writeAsCsvFile(Company company, String fileName) throws OutputWriterException {
    try (FileWriter fileWriter = new FileWriter(fileName)) {
      fileWriter.append(String.format("%s%n", company));
      log.info("File created successfully {} on {}", fileName, LocalDateTime.now());
    } catch (IOException e) {
      log.error("File creation failed for file {}", fileName, e);
      throw new OutputWriterException(e);
    }
  }
}
