package hyper.reports.database.service.store;

import hyper.reports.database.repository.StoreRepository;
import hyper.reports.database.repository.specification.store.FindAllStoresOwnedByCompany;
import hyper.reports.entity.Company;
import hyper.reports.entity.Store;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.RepositoryException;

import java.util.List;

public class StoreServiceImpl implements StoreService<Store> {

  private StoreRepository<Store> storeRepository;

  public StoreServiceImpl() {
    this.storeRepository = new StoreRepository<>();
  }

  @Override
  public Store update(Store entity) throws RepositoryException, ConnectionException {
    return storeRepository.update(entity);
  }

  @Override
  public Store delete(Store entity) throws RepositoryException, ConnectionException {
    return storeRepository.delete(entity);
  }

  @Override
  public void insertStoresInBatch(Company entry) throws ConnectionException, RepositoryException {
    storeRepository.insertStoresInBatch(entry);
  }

  @Override
  public List<Store> getAllStoresOwnedByCompany(int uuid) throws ConnectionException, RepositoryException {
    return storeRepository.query(new FindAllStoresOwnedByCompany(uuid));
  }
}
