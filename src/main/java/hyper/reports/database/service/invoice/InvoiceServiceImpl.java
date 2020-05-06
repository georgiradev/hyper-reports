package hyper.reports.database.service.invoice;

import hyper.reports.database.repository.InvoiceRepository;
import hyper.reports.database.repository.specification.invoice.FindInvoicesByStoreAndDay;
import hyper.reports.database.repository.specification.invoice.FindInvoicesByStoreAndMonth;
import hyper.reports.database.repository.specification.invoice.FindInvoicesByStoreMonthAndCard;
import hyper.reports.entity.Invoice;
import hyper.reports.entity.Store;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.RepositoryException;

import java.util.List;

public class InvoiceServiceImpl implements InvoiceService<Invoice> {

  private InvoiceRepository<Invoice> invoiceRepository;

  public InvoiceServiceImpl() {
    invoiceRepository = new InvoiceRepository<>();
  }

  @Override
  public void insertBatch(Store store) throws ConnectionException, RepositoryException {
    invoiceRepository.insertBatch(store);
  }

  @Override
  public Invoice update(Invoice entity) throws RepositoryException, ConnectionException {
    return invoiceRepository.update(entity);
  }

  @Override
  public Invoice delete(Invoice entity) throws RepositoryException, ConnectionException {
    return invoiceRepository.delete(entity);
  }

  @Override
  public List<Invoice> getInvoicesByStoreDate(int storeId, int month, int year) throws ConnectionException, RepositoryException {
    return invoiceRepository.query(new FindInvoicesByStoreAndMonth(storeId, month, year));
  }

  @Override
  public List<Invoice> getInvoicesByStoreDate(int storeId, int month, int day, int year) throws ConnectionException, RepositoryException {
    return invoiceRepository.query(new FindInvoicesByStoreAndDay(storeId, month, day, year));
  }

  @Override
  public List<Invoice> getInvoicesByDateAndCard(int companyId, int month, int year, int cardId) throws ConnectionException, RepositoryException {
    return invoiceRepository.query(new FindInvoicesByStoreMonthAndCard(companyId, month, year, cardId));
  }
}
