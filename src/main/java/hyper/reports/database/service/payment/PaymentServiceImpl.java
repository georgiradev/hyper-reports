package hyper.reports.database.service.payment;

import hyper.reports.database.repository.PaymentRepository;
import hyper.reports.entity.PaymentType;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.RepositoryException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService<PaymentType> {

  private PaymentRepository<PaymentType> paymentRepository;

  @Override
  public PaymentType insert(PaymentType entity) throws RepositoryException, ConnectionException {
    return paymentRepository.insert(entity);
  }

  @Override
  public PaymentType update(PaymentType entity) throws RepositoryException, ConnectionException {
    return paymentRepository.update(entity);
  }

  @Override
  public PaymentType delete(PaymentType entity) throws RepositoryException, ConnectionException {
    return paymentRepository.delete(entity);
  }
}
