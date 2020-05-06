package hyper.reports.database.service.company;

import hyper.reports.database.repository.CompanyRepository;
import hyper.reports.database.repository.specification.company.FindByName;
import hyper.reports.database.repository.specification.company.FindByNameAndAddressSpecification;
import hyper.reports.database.service.store.StoreService;
import hyper.reports.database.service.store.StoreServiceImpl;
import hyper.reports.entity.Company;
import hyper.reports.entity.Store;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.RepositoryException;

import java.util.List;
import java.util.Optional;

public class CompanyServiceImpl implements CompanyService<Company> {

  private CompanyRepository<Company> companyRepository;
  private StoreService<Store> storeService;

  public CompanyServiceImpl() {
    this.companyRepository = new CompanyRepository<>();
    this.storeService = new StoreServiceImpl();
  }

  @Override
  public Optional<Company> getOneByNameAndAddress(String name, String address)
      throws RepositoryException, ConnectionException {
    List<Company> list =
        companyRepository.query(new FindByNameAndAddressSpecification(name, address));
    if (list.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(list.get(0));
    }
  }

  @Override
  public Company insert(Company entity) throws RepositoryException, ConnectionException {
    Optional<Company> companyOptional =
        getOneByNameAndAddress(entity.getName(), entity.getAddress());
    if (companyOptional.isEmpty()) {
      companyRepository.insert(entity);
    }
    storeService.insertStoresInBatch(entity);
    return entity;
  }

  @Override
  public Company update(Company entity) throws RepositoryException, ConnectionException {
    return companyRepository.update(entity);
  }

  @Override
  public Company delete(Company entity) throws RepositoryException, ConnectionException {
    return companyRepository.delete(entity);
  }

  @Override
  public Company getByName(String companyName) throws ConnectionException, RepositoryException {
    List<Company> query = companyRepository.query(new FindByName(companyName));

    if (query.isEmpty()) {
      return null;
    } else {
      return query.get(0);
    }
  }

  @Override
  public Company findByName(String companyName) throws ConnectionException, RepositoryException {
    List<Company> query = companyRepository.query(new FindByName(companyName));
    if (query.isEmpty()) {
      return null;
    } else {
      return query.get(0);
    }
  }
}
