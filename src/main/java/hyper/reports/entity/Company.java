package hyper.reports.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.jooq.Table;

import java.util.List;
import java.util.Set;

@Data
public class Company extends Base {

  private String name;

  private String address;

  private List<Store> stores;
}
