package hyper.reports.report;

import java.time.Month;
import java.util.Map;

public interface ReportData extends Comparable<ReportData> {

  String getName();

  Map<String, Double> getMonthTurnoverMap();

  void putByMonthNumber(int month, double turnoverValue);

  void putByMonth(Month month, double turnoverValue);

  void putByDay(int day, double turnover);

  double getTurnover();

  void setName(String name);

  void calculateTurnover();
}
