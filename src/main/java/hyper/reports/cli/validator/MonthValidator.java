package hyper.reports.cli.validator;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import java.time.DateTimeException;
import java.time.Month;

public class MonthValidator implements IParameterValidator {

  @Override
  public void validate(String name, String value) {
    try {
      Month.of(Integer.parseInt(value));
    } catch (DateTimeException | NumberFormatException e) {
      String message = String.format("%s is invalid month", value);
      throw new ParameterException(message);
    }
  }
}
