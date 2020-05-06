package hyper.reports.cli.validator;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import java.util.ArrayList;
import java.util.List;

public class AggregationValidator implements IParameterValidator {

    private static List<String> aggregations = new ArrayList<>();

    static {
        aggregations.add("store");
        aggregations.add("receipt");
        aggregations.add("invoice");
        aggregations.add("payment");
        aggregations.add("cash");
        aggregations.add("card");
    }

    @Override
    public void validate(String name, String value) {
        if(!aggregations.contains(value)) {
            String message =
                    String.format("%s is invalid aggregation. You can specify %s", value, aggregations);
            throw new ParameterException(message);
        }
    }
}
