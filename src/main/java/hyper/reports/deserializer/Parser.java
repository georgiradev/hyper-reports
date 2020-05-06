package hyper.reports.deserializer;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import hyper.reports.entity.Company;
import hyper.reports.exception.ParserException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

@Slf4j
public class Parser {

  private Parser() {}

  private static final XmlMapper XML_MAPPER = new XmlMapper();

  public static Company parse(File file) throws ParserException {
    try {
      String readContent = Files.readString(file.toPath());

      log.info("Deserialization successful for file {}", file.getName());
      return XML_MAPPER.readValue(readContent, Company.class);
    } catch (IOException e) {
      throw new ParserException(e);
    }
  }
}
