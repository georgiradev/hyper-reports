package hyper.reports.database.service.payment;

import hyper.reports.entity.PaymentType;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.RepositoryException;

public interface PaymentService<T> {

    PaymentType insert(PaymentType entity) throws RepositoryException, ConnectionException;

    PaymentType update(PaymentType entity) throws RepositoryException, ConnectionException;

    PaymentType delete(PaymentType entity) throws RepositoryException, ConnectionException;
}
