package hyper.reports.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class CardDetails extends Base {

  @JacksonXmlProperty(localName = "cardtype")
  private String cardType;

  private String number;

  @JacksonXmlProperty(localName = "contactless")
  private boolean contactLess;
}
