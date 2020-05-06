package hyper.reports.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PaymentType {

  @JsonProperty("cash")
  CASH,

  @JsonProperty("card")
  CARD
}
