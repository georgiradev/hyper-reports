package hyper.reports.report;

import lombok.Getter;

import java.time.Month;
import java.time.YearMonth;
import java.util.*;

@Getter
public class ReportUtils {

  private List<Month> months = new ArrayList<>();

  public ReportUtils() {
    this.months = new ArrayList<>();
    months.add(Month.JANUARY);
    months.add(Month.FEBRUARY);
    months.add(Month.MARCH);
    months.add(Month.APRIL);
    months.add(Month.MAY);
    months.add(Month.JUNE);
    months.add(Month.JULY);
    months.add(Month.AUGUST);
    months.add(Month.SEPTEMBER);
    months.add(Month.OCTOBER);
    months.add(Month.NOVEMBER);
    months.add(Month.DECEMBER);
  }

  public List<Month> getMonthsByQuarter(String quarter) {
    List<Month> months = new ArrayList<>();
    switch (quarter.toLowerCase()) {
      case "q1":
        months.add(Month.JANUARY);
        months.add(Month.FEBRUARY);
        months.add(Month.MARCH);
        break;
      case "q2":
        months.add(Month.APRIL);
        months.add(Month.MAY);
        months.add(Month.JUNE);
        break;
      case "q3":
        months.add(Month.JULY);
        months.add(Month.AUGUST);
        months.add(Month.SEPTEMBER);
        break;
      case "q4":
        months.add(Month.OCTOBER);
        months.add(Month.NOVEMBER);
        months.add(Month.DECEMBER);
        break;

      default:
        throw new IllegalStateException("Unexpected value: " + quarter);
    }
    return months;
  }

  public int daysInMonth(int month, int year) {
    YearMonth yearMonth = YearMonth.of(year, month);
    return yearMonth.lengthOfMonth();
  }
}
