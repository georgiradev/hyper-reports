package hyper.reports.database.service.customer;

import hyper.reports.entity.Customer;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.RepositoryException;

import java.util.Optional;

public interface CustomerService<T> {

  Optional<Customer> getOne(Customer customer) throws RepositoryException, ConnectionException;

  Customer insert(Customer entity) throws RepositoryException, ConnectionException;

  Customer update(Customer entity) throws RepositoryException, ConnectionException;

  Customer delete(Customer entity) throws RepositoryException, ConnectionException;
}
