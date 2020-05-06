package hyper.reports.report.parser;

import hyper.reports.cli.HyperReportsCommands;
import hyper.reports.report.Report;
import hyper.reports.report.ReportData;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Month;
import java.util.List;
import java.util.Map;

@Slf4j
public class XlsxParser {

  public void exportAsXlsx(Report report, String path, HyperReportsCommands parameters) {

    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet(parameters.getAggregation());
      sheet.setDefaultColumnWidth(15);
      Font headerFont = workbook.createFont();
      headerFont.setBold(true);

      CellStyle headerCellStyle = workbook.createCellStyle();
      headerCellStyle.setFont(headerFont);
      headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

      CellStyle cellStyle = workbook.createCellStyle();
      cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
      cellStyle.setAlignment(HorizontalAlignment.CENTER);

      List<String> cellFields = report.getCellFields();
      Row headerRow = sheet.createRow(0);

      for (int i = 0; i < cellFields.size(); i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(cellFields.get(i));
        cell.setCellStyle(headerCellStyle);
      }

      int cellCounter = 0;
      int rowCounter = 1;

      for (ReportData data : report.getDataList()) {
        Row row = sheet.createRow(rowCounter++);
        row.createCell(cellCounter).setCellValue(data.getName());
        if (data.getName() != null && !data.getName().equals("")) {
          row.createCell(cellCounter++).setCellValue(data.getName());
        }
        for (Map.Entry<String, Double> entry : data.getMonthTurnoverMap().entrySet()) {
          Cell cell = row.createCell(cellCounter++);
          cell.setCellValue(entry.getValue());
          cell.setCellStyle(cellStyle);
        }
        Cell totalCell = row.createCell(cellCounter);
        totalCell.setCellValue(data.getTurnover());
        totalCell.setCellStyle(cellStyle);
        cellCounter = 0;
      }

      String excelFileName =
          "report"
              + monthOrYear(parameters)
              + "-"
              + parameters.getYear()
              + "-"
              + parameters.getCompanyName()
              + "-"
              + parameters.getAggregation();
      FileOutputStream fileOutputStream =
          new FileOutputStream(new File(path + excelFileName + ".xlsx"));
      workbook.write(fileOutputStream);
      System.out.println("File " + excelFileName + ".xlsx has been created in directory " + path);
      fileOutputStream.close();
    } catch (IOException e) {
      log.error("Export as xlsx file failed", e);
    }
  }

  private String monthOrYear(HyperReportsCommands parameters) {
    if (parameters.getMonth() != 0) {
      String monthName = Month.of(parameters.getMonth()).toString();
      return "-" + monthName.substring(0, 1) + monthName.substring(1).toLowerCase();
    } else if (parameters.getQuarter() != null) {
      return "-" + parameters.getQuarter();
    }
    return "";
  }
}
