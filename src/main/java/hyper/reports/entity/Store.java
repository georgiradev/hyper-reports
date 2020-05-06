package hyper.reports.entity;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class Store extends Base {

  private int companyId;

  private String name;

  private String address;

  private List<Receipt> receipts;

  private List<Invoice> invoices;
}
