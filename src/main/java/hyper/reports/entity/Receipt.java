package hyper.reports.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import hyper.reports.entity.adapter.LocalDateTimeDeserializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Receipt extends Base {

  private Integer cardDetailsId;

  private int paymentTypeId;

  private int storeId;

  private double total;

  @JacksonXmlProperty(localName = "datetime")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime dateTime;

  private PaymentType payment;

  @JacksonXmlProperty(localName = "carddetails")
  private CardDetails cardDetails;
}
