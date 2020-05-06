package hyper.reports.report;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Month;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class ReportDataImpl implements ReportData {

  private String name;

  private Map<String, Double> monthTurnoverMap = new LinkedHashMap<>();

  private BigDecimal turnover = new BigDecimal(0);

  public void putByMonth(Month month, double turnover) {
    this.monthTurnoverMap.put(month.name(), turnover);
  }

  public void putByMonthNumber(int month, double turnover) {
    switch (month) {
      case 3:
        this.monthTurnoverMap.put("Q1", turnover);
        break;
      case 6:
        this.monthTurnoverMap.put("Q2", turnover);
        break;
      case 9:
        this.monthTurnoverMap.put("Q3", turnover);
        break;
      case 12:
        this.monthTurnoverMap.put("Q4", turnover);
        break;
      default:
        throw new IllegalStateException("Unexpected value: " + month);
    }
  }

  public void putByDay(int day, double turnover) {
    this.monthTurnoverMap.put(String.valueOf(day), turnover);
  }

  public void calculateTurnover() {
    for (Map.Entry<String, Double> entry : monthTurnoverMap.entrySet()) {
      turnover = turnover.add(BigDecimal.valueOf(entry.getValue()));
    }
  }

  @Override
  public int compareTo(ReportData reportData) {
    return Double.compare(this.turnover.doubleValue(), reportData.getTurnover());
  }

  public double getTurnover() {
    return turnover.doubleValue();
  }
}
