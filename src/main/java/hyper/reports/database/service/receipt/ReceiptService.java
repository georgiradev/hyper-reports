package hyper.reports.database.service.receipt;

import hyper.reports.entity.Receipt;
import hyper.reports.entity.Store;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.RepositoryException;

import java.util.List;
import java.util.Optional;

public interface ReceiptService<T> {

  void insertBatch(Store store) throws ConnectionException, RepositoryException;

  Optional<Receipt> getOneByTotalStoreIdPayment(Receipt receipt)
      throws RepositoryException, ConnectionException;

  Receipt update(Receipt entity) throws ConnectionException, RepositoryException;

  Receipt delete(Receipt entity) throws ConnectionException, RepositoryException;

  List<T> getReceiptsByStoreDate(int uuid, int value, int year)
      throws ConnectionException, RepositoryException;

  List<T> getReceiptsByStoreDate(int uuid, int month, int day, int year)
      throws ConnectionException, RepositoryException;

  List<T> getReceiptsByDateAndCard(int companyId, int month, int year, int paymentTypeId)
      throws ConnectionException, RepositoryException;
}
