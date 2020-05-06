package hyper.reports.cli.validator;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class YearValidator implements IParameterValidator {

  @Override
  public void validate(String name, String value) {
    try {
      Year.parse(value, DateTimeFormatter.ofPattern("yyyy"));
    } catch (DateTimeParseException | ParameterException e) {
      String message = String.format("%s is invalid year", value);
      throw new ParameterException(message);
    }
  }
}
