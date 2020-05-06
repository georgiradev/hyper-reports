package hyper.reports.report;

import java.util.ArrayList;
import java.util.List;

public class Report {

  private List<String> cellFields;
  private List<ReportData> dataList;

  public List<ReportData> getDataList() {
    if (dataList == null) {
      dataList = new ArrayList<>();
    }

    return dataList;
  }

  public void setDataList(List<ReportData> dataList) {
    this.dataList = dataList;
  }

  public void setCellFields(String firstField) {
    cellFields = new ArrayList<>();

    if (!firstField.equalsIgnoreCase("")) {
      cellFields.add(firstField);
    }
    cellFields.addAll(dataList.get(0).getMonthTurnoverMap().keySet());
    cellFields.add("Total");
  }

  public List<String> getCellFields() {
    return this.cellFields;
  }

  @Override
  public String toString() {
    return "Report{" + "cellFields=" + cellFields + ", reportDataList=" + dataList + '}';
  }
}
