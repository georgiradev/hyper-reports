package hyper.reports.database.service.customer;

import hyper.reports.database.repository.CustomerRepository;
import hyper.reports.database.repository.specification.customer.FindByNameAndAddressSpecification;
import hyper.reports.entity.Customer;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.RepositoryException;

import java.util.List;
import java.util.Optional;

public class CustomerServiceImpl implements CustomerService<Customer> {

  private CustomerRepository<Customer> customerRepository;

  public CustomerServiceImpl() {
    this.customerRepository = new CustomerRepository<>();
  }

  @Override
  public Optional<Customer> getOne(Customer customer)
      throws RepositoryException, ConnectionException {
    List<Customer> list =
        customerRepository.query(
            new FindByNameAndAddressSpecification(
                customer.getUuid(), customer.getName(), customer.getAddress()));
    if (list.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(list.get(0));
    }
  }

  @Override
  public Customer insert(Customer entity) throws RepositoryException, ConnectionException {
    Optional<Customer> customerOptional = getOne(entity);
    if (customerOptional.isPresent()) {
      return customerOptional.get();
    } else {
      return customerRepository.insert(entity);
    }
  }

  @Override
  public Customer update(Customer entity) throws RepositoryException, ConnectionException {
    return customerRepository.update(entity);
  }

  @Override
  public Customer delete(Customer entity) throws RepositoryException, ConnectionException {
    return customerRepository.delete(entity);
  }
}
