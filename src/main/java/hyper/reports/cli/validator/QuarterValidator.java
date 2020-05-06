package hyper.reports.cli.validator;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import java.util.ArrayList;
import java.util.List;

public class QuarterValidator implements IParameterValidator {

  private static List<String> quarters = new ArrayList<>();

  static {
    quarters.add("Q1");
    quarters.add("Q2");
    quarters.add("Q3");
    quarters.add("Q4");
    quarters.add("q1");
    quarters.add("q2");
    quarters.add("q3");
    quarters.add("q4");
  }

  @Override
  public void validate(String name, String value) {
    if (!quarters.contains(value)) {
      String message = String.format("%s is invalid quarter", value);
      throw new ParameterException(message);
    }
  }
}
