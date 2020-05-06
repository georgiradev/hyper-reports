package hyper.reports.report;

import hyper.reports.database.service.invoice.InvoiceService;
import hyper.reports.database.service.invoice.InvoiceServiceImpl;
import hyper.reports.database.service.receipt.ReceiptService;
import hyper.reports.database.service.receipt.ReceiptServiceImpl;
import hyper.reports.database.service.store.StoreService;
import hyper.reports.database.service.store.StoreServiceImpl;
import hyper.reports.entity.Invoice;
import hyper.reports.entity.Receipt;
import hyper.reports.entity.Store;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.RepositoryException;

import java.math.BigDecimal;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class ReportEngine {
  private final StoreService<Store> storeService;
  private final InvoiceService<Invoice> invoiceService;
  private final ReceiptService<Receipt> receiptService;

  public ReportEngine() {
    this.storeService = new StoreServiceImpl();
    this.invoiceService = new InvoiceServiceImpl();
    this.receiptService = new ReceiptServiceImpl();
  }

  public Report createInvoiceYearReport(int year, int companyID)
      throws ConnectionException, RepositoryException {

    List<Store> stores = storeService.getAllStoresOwnedByCompany(companyID);
    Report report = new Report();
    ReportData data = new ReportDataImpl();
    ReportUtils reportUtils = new ReportUtils();
    double turnover = 0;

    for (int i = 0; i < reportUtils.getMonths().size(); i++) {

      List<Invoice> allInvoices = new ArrayList<>();
      int monthValue = reportUtils.getMonths().get(i).getValue();
      for (Store store : stores) {

        List<Invoice> invoices =
            invoiceService.getInvoicesByStoreDate(store.getUuid(), monthValue, year);
        allInvoices.addAll(invoices);
      }

      turnover += calculateTurnover(new ArrayList<>(), allInvoices);

      if ((i + 1) % 3 == 0) {
        data.putByMonthNumber(monthValue, turnover);
        allInvoices.clear();
        turnover = 0;
      }
    }

    data.calculateTurnover();
    report.getDataList().add(data);

    report.setCellFields("");
    return report;
  }

  public Report createReceiptYearReport(int year, int companyId)
      throws ConnectionException, RepositoryException {

    List<Store> stores = storeService.getAllStoresOwnedByCompany(companyId);
    Report report = new Report();
    ReportData data = new ReportDataImpl();
    ReportUtils reportUtils = new ReportUtils();
    double turnover = 0;

    for (int i = 0; i < reportUtils.getMonths().size(); i++) {

      List<Receipt> allReceipts = new ArrayList<>();
      int monthValue = reportUtils.getMonths().get(i).getValue();

      for (Store store : stores) {
        List<Receipt> receipts =
            receiptService.getReceiptsByStoreDate(store.getUuid(), monthValue, year);
        allReceipts.addAll(receipts);
      }

      turnover += calculateTurnover(allReceipts, new ArrayList<>());

      if ((i + 1) % 3 == 0) {
        data.putByMonthNumber(monthValue, turnover);
        allReceipts.clear();
        turnover = 0;
      }
    }

    data.calculateTurnover();
    report.getDataList().add(data);

    report.setCellFields("");
    return report;
  }

  private static double calculateTurnover(List<Receipt> receipts, List<Invoice> invoices) {

    BigDecimal total = new BigDecimal(0);

    for (Receipt receipt : receipts) {
      total = total.add(BigDecimal.valueOf(receipt.getTotal()));
    }

    for (Invoice invoice : invoices) {
      total = total.add(BigDecimal.valueOf(invoice.getTotal()));
    }

    return total.doubleValue();
  }

  public Report createInvoiceQuarterReport(int year, String quarter, int companyID)
      throws ConnectionException, RepositoryException {

    List<Store> stores = storeService.getAllStoresOwnedByCompany(companyID);
    Report report = new Report();
    ReportData data = new ReportDataImpl();
    ReportUtils reportUtils = new ReportUtils();
    List<Month> monthsByQuarter = reportUtils.getMonthsByQuarter(quarter);
    double turnover = 0;

    for (int i = 0; i < monthsByQuarter.size(); i++) {

      List<Invoice> allInvoices = new ArrayList<>();
      int monthValue = monthsByQuarter.get(i).getValue();
      for (Store store : stores) {

        List<Invoice> invoices =
            invoiceService.getInvoicesByStoreDate(store.getUuid(), monthValue, year);
        allInvoices.addAll(invoices);
      }

      turnover += calculateTurnover(new ArrayList<>(), allInvoices);

      data.putByMonth(monthsByQuarter.get(i), turnover);
      allInvoices.clear();
      turnover = 0;
    }

    data.calculateTurnover();
    report.getDataList().add(data);

    report.setCellFields("");
    return report;
  }

  public Report createReceiptQuarterReport(int year, String quarter, int companyID)
      throws ConnectionException, RepositoryException {

    List<Store> stores = storeService.getAllStoresOwnedByCompany(companyID);
    Report report = new Report();
    ReportData data = new ReportDataImpl();
    ReportUtils reportUtils = new ReportUtils();
    List<Month> monthsByQuarter = reportUtils.getMonthsByQuarter(quarter);
    double turnover = 0;

    for (int i = 0; i < monthsByQuarter.size(); i++) {

      List<Receipt> allReceipts = new ArrayList<>();
      int monthValue = monthsByQuarter.get(i).getValue();
      for (Store store : stores) {

        List<Receipt> receipts =
            receiptService.getReceiptsByStoreDate(store.getUuid(), monthValue, year);
        allReceipts.addAll(receipts);
      }

      turnover += calculateTurnover(allReceipts, new ArrayList<>());

      data.putByMonth(monthsByQuarter.get(i), turnover);
      allReceipts.clear();
      turnover = 0;
    }

    data.calculateTurnover();
    report.getDataList().add(data);

    report.setCellFields("");
    return report;
  }

  public Report createInvoiceMonthReport(int year, int month, int companyID)
      throws ConnectionException, RepositoryException {
    List<Store> stores = storeService.getAllStoresOwnedByCompany(companyID);
    Report report = new Report();
    ReportData data = new ReportDataImpl();
    ReportUtils reportUtils = new ReportUtils();
    int days = reportUtils.daysInMonth(month, year);
    double turnover = 0;

    for (int i = 1; i <= days; i++) {
      List<Invoice> allInvoices = new ArrayList<>();

      for (Store store : stores) {

        int day = i;
        List<Invoice> invoices =
            invoiceService.getInvoicesByStoreDate(store.getUuid(), month, day, year);
        allInvoices.addAll(invoices);
      }
      turnover += calculateTurnover(new ArrayList<>(), allInvoices);

      data.putByDay(i, turnover);
      allInvoices.clear();
      turnover = 0;
    }

    data.calculateTurnover();
    report.getDataList().add(data);

    report.setCellFields("");
    return report;
  }

  public Report createReceiptMonthReport(int year, int month, int companyID)
      throws ConnectionException, RepositoryException {

    List<Store> stores = storeService.getAllStoresOwnedByCompany(companyID);
    Report report = new Report();
    ReportData data = new ReportDataImpl();
    ReportUtils reportUtils = new ReportUtils();
    int days = reportUtils.daysInMonth(month, year);
    double turnover = 0;

    for (int i = 1; i <= days; i++) {
      List<Receipt> allReceipts = new ArrayList<>();

      for (Store store : stores) {

        int day = i;
        List<Receipt> invoices =
            receiptService.getReceiptsByStoreDate(store.getUuid(), month, day, year);
        allReceipts.addAll(invoices);
      }
      turnover += calculateTurnover(allReceipts, new ArrayList<>());

      data.putByDay(i, turnover);
      allReceipts.clear();
      turnover = 0;
    }

    data.calculateTurnover();
    report.getDataList().add(data);

    report.setCellFields("");
    return report;
  }

  public Report createStoreYearReport(int year, int companyID, String order)
      throws ConnectionException, RepositoryException {

    List<Store> stores = storeService.getAllStoresOwnedByCompany(companyID);
    Report report = new Report();
    ReportUtils reportUtils = new ReportUtils();
    double turnover = 0;

    List<Invoice> allInvoices = new ArrayList<>();
    List<Receipt> allReceipts = new ArrayList<>();

    for (Store store : stores) {
      ReportData data = new ReportDataImpl();
      data.setName(store.getName());

      for (int i = 0; i < reportUtils.getMonths().size(); i++) {

        int monthValue = reportUtils.getMonths().get(i).getValue();

        List<Invoice> invoices =
            invoiceService.getInvoicesByStoreDate(store.getUuid(), monthValue, year);
        List<Receipt> receipts =
            receiptService.getReceiptsByStoreDate(store.getUuid(), monthValue, year);
        allInvoices.addAll(invoices);
        allReceipts.addAll(receipts);

        turnover += calculateTurnover(allReceipts, allInvoices);
        allInvoices.clear();
        allReceipts.clear();

        if ((i + 1) % 3 == 0) {
          data.putByMonthNumber(monthValue, turnover);
          turnover = 0;
        }
      }
      data.calculateTurnover();
      report.getDataList().add(data);
    }

    report.setCellFields("Store");
    return report;
  }

  public Report createStoreQuarterReport(int year, String quarter, int companyID, String order)
      throws ConnectionException, RepositoryException {
    List<Store> stores = storeService.getAllStoresOwnedByCompany(companyID);
    Report report = new Report();
    ReportUtils reportUtils = new ReportUtils();
    double turnover = 0;

    List<Invoice> allInvoices = new ArrayList<>();
    List<Receipt> allReceipts = new ArrayList<>();

    List<Month> monthsByQuarter = reportUtils.getMonthsByQuarter(quarter);

    for (Store store : stores) {
      ReportData data = new ReportDataImpl();
      data.setName(store.getName());

      for (int i = 0; i < monthsByQuarter.size(); i++) {

        int monthValue = monthsByQuarter.get(i).getValue();

        List<Invoice> invoices =
            invoiceService.getInvoicesByStoreDate(store.getUuid(), monthValue, year);
        List<Receipt> receipts =
            receiptService.getReceiptsByStoreDate(store.getUuid(), monthValue, year);
        allInvoices.addAll(invoices);
        allReceipts.addAll(receipts);

        turnover += calculateTurnover(allReceipts, allInvoices);
        allInvoices.clear();
        allReceipts.clear();

        data.putByMonth(monthsByQuarter.get(i), turnover);
        turnover = 0;
      }
      data.calculateTurnover();
      report.getDataList().add(data);
    }

    report.setCellFields("Store");
    return report;
  }

  public Report createStoreMonthReport(int year, int month, int companyID, String order)
      throws ConnectionException, RepositoryException {
    List<Store> stores = storeService.getAllStoresOwnedByCompany(companyID);
    Report report = new Report();
    ReportUtils reportUtils = new ReportUtils();
    double turnover = 0;

    List<Invoice> allInvoices = new ArrayList<>();
    List<Receipt> allReceipts = new ArrayList<>();

    int days = reportUtils.daysInMonth(month, year);

    for (Store store : stores) {
      ReportData data = new ReportDataImpl();
      data.setName(store.getName());

      for (int i = 1; i <= days; i++) {

        List<Invoice> invoices =
            invoiceService.getInvoicesByStoreDate(store.getUuid(), month, i, year);
        List<Receipt> receipts =
            receiptService.getReceiptsByStoreDate(store.getUuid(), month, i, year);
        allInvoices.addAll(invoices);
        allReceipts.addAll(receipts);

        turnover += calculateTurnover(allReceipts, allInvoices);
        allInvoices.clear();
        allReceipts.clear();

        data.putByDay(i, turnover);
        turnover = 0;
      }
      data.calculateTurnover();
      report.getDataList().add(data);
    }

    report.setCellFields("Store");
    return report;
  }

  public Report createPaymentYearReport(int year, int companyId)
      throws ConnectionException, RepositoryException {

    Report report = new Report();
    ReportUtils reportUtils = new ReportUtils();
    double turnover = 0;

    List<Invoice> allInvoices = new ArrayList<>();
    List<Receipt> allReceipts = new ArrayList<>();

    for (int paymentId = 1; paymentId <= 2; paymentId++) {
      ReportData data = new ReportDataImpl();
      data.setName(paymentId == 1 ? "cash" : "card");

      for (int i = 0; i < reportUtils.getMonths().size(); i++) {

        int monthValue = reportUtils.getMonths().get(i).getValue();

        List<Invoice> invoices =
            invoiceService.getInvoicesByDateAndCard(companyId, monthValue, year, paymentId);
        List<Receipt> receipts =
            receiptService.getReceiptsByDateAndCard(companyId, monthValue, year, paymentId);
        allInvoices.addAll(invoices);
        allReceipts.addAll(receipts);

        turnover += calculateTurnover(allReceipts, allInvoices);
        allInvoices.clear();
        allReceipts.clear();

        if ((i + 1) % 3 == 0) {
          data.putByMonthNumber(monthValue, turnover);
          turnover = 0;
        }
      }

      data.calculateTurnover();
      report.getDataList().add(data);
    }
    report.setCellFields("Payment");
    return report;
  }
}
