package hyper.reports.database.service.store;

import hyper.reports.entity.Company;
import hyper.reports.entity.Store;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.RepositoryException;

import java.util.List;

public interface StoreService<T> {

  Store update(Store entity) throws RepositoryException, ConnectionException;

  Store delete(Store entity) throws RepositoryException, ConnectionException;

  void insertStoresInBatch(Company company) throws RepositoryException, ConnectionException;

  List<T> getAllStoresOwnedByCompany(int uuid) throws ConnectionException, RepositoryException;
}
