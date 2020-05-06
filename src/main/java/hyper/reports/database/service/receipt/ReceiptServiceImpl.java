package hyper.reports.database.service.receipt;

import hyper.reports.database.repository.ReceiptRepository;
import hyper.reports.database.repository.specification.receipt.FindReceiptsByStoreAndDay;
import hyper.reports.database.repository.specification.receipt.FindReceiptsByStoreAndMonth;
import hyper.reports.database.repository.specification.receipt.FindPresentSpecification;
import hyper.reports.database.repository.specification.receipt.FindReceiptsByStoreDayAndCard;
import hyper.reports.entity.Receipt;
import hyper.reports.entity.Store;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.RepositoryException;

import java.util.List;
import java.util.Optional;

public class ReceiptServiceImpl implements ReceiptService<Receipt> {

  private ReceiptRepository<Receipt> receiptRepository;

  public ReceiptServiceImpl() {
    this.receiptRepository = new ReceiptRepository<>();
  }

  @Override
  public void insertBatch(Store store) throws ConnectionException, RepositoryException {
    receiptRepository.insertInBatch(store);
  }

  @Override
  public Optional<Receipt> getOneByTotalStoreIdPayment(Receipt receipt)
      throws RepositoryException, ConnectionException {
    List<Receipt> list =
        receiptRepository.query(
            new FindPresentSpecification(
                receipt.getTotal(), receipt.getStoreId(), receipt.getPaymentTypeId()));
    if (list.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(list.get(0));
    }
  }

  @Override
  public Receipt update(Receipt entity) throws ConnectionException, RepositoryException {
    return receiptRepository.update(entity);
  }

  @Override
  public Receipt delete(Receipt entity) throws ConnectionException, RepositoryException {
    return receiptRepository.delete(entity);
  }

  @Override
  public List<Receipt> getReceiptsByStoreDate(int storeId, int month, int year) throws ConnectionException, RepositoryException {
    return receiptRepository.query(new FindReceiptsByStoreAndMonth(storeId, month, year));
  }

  @Override
  public List<Receipt> getReceiptsByStoreDate(int storeId, int month, int day, int year) throws ConnectionException, RepositoryException {
    return receiptRepository.query(new FindReceiptsByStoreAndDay(storeId, month, day, year));
  }

  @Override
  public List<Receipt> getReceiptsByDateAndCard(int companyId, int monthValue, int year, int paymentTypeId) throws ConnectionException, RepositoryException {
    return receiptRepository.query(new FindReceiptsByStoreDayAndCard(companyId, monthValue, year, paymentTypeId));
  }
}
