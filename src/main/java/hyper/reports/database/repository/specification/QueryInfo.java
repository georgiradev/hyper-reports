package hyper.reports.database.repository.specification;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class QueryInfo {

  private String sql;

  private Map<Integer, Object> placeholders;
}
