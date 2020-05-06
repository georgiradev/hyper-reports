package hyper.reports.database.service.invoice;

import hyper.reports.entity.Invoice;
import hyper.reports.entity.Store;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.RepositoryException;

import java.util.List;

public interface InvoiceService<T> {

  void insertBatch(Store store) throws ConnectionException, RepositoryException;

  Invoice update(Invoice entity) throws RepositoryException, ConnectionException;

  Invoice delete(Invoice entity) throws RepositoryException, ConnectionException;

  List<T> getInvoicesByStoreDate(int uuid, int value, int year) throws ConnectionException, RepositoryException;

  List<T> getInvoicesByStoreDate(int uuid, int month, int day, int year) throws ConnectionException, RepositoryException;

    List<T> getInvoicesByDateAndCard(int companyId, int month, int year, int paymentTypeId) throws ConnectionException, RepositoryException;
}
