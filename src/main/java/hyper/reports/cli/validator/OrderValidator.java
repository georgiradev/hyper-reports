package hyper.reports.cli.validator;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class OrderValidator implements IParameterValidator {

  @Override
  public void validate(String name, String value) {

    if (!value.equalsIgnoreCase("asc") && !value.equalsIgnoreCase("desc") && !value.equals("\\s+")) {
      String message =
          String.format("%s is invalid order. You can specify only asc or desc", value);
      throw new ParameterException(message);
    }
  }
}
