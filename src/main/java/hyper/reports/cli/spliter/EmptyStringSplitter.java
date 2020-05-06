package hyper.reports.cli.spliter;

import com.beust.jcommander.converters.IParameterSplitter;

import java.util.Arrays;
import java.util.List;

public class EmptyStringSplitter implements IParameterSplitter {
  @Override
  public List<String> split(String value) {
    return Arrays.asList(value.split("\\s+"));
  }
}
