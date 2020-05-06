package hyper.reports.database.service.company;

import hyper.reports.entity.Company;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.RepositoryException;

import java.util.Optional;

public interface CompanyService<T> {

  Optional<Company> getOneByNameAndAddress(String name, String address)
      throws RepositoryException, ConnectionException;

  Company insert(Company entity) throws RepositoryException, ConnectionException;

  Company update(Company entity) throws RepositoryException, ConnectionException;

  Company delete(Company entity) throws RepositoryException, ConnectionException;

  Company getByName(String companyName) throws ConnectionException, RepositoryException;

  Company findByName(String companyName) throws ConnectionException, RepositoryException;
}
