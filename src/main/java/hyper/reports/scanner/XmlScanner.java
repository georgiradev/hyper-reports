package hyper.reports.scanner;

import hyper.reports.deserializer.Parser;
import hyper.reports.entity.Company;
import hyper.reports.exception.ParserException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XmlScanner {

  public List<Company> getResourceFiles(String path) throws ParserException {
    List<Company> companies = new ArrayList<>();
    File dir = new File(path);

    if (dir.exists() && dir.isDirectory()) {
      File[] files = dir.listFiles((d, name) -> name.endsWith(".xml"));

      if (files != null) {
        for (File file : files) {
          Company company = Parser.parse(file);
          companies.add(company);
        }
      }
    }
    return companies;
  }
}
