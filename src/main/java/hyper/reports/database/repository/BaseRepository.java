package hyper.reports.database.repository;

import hyper.reports.database.repository.specification.Specification;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.RepositoryException;

import java.util.List;

public interface BaseRepository<T> {

  T update(T entry) throws RepositoryException, ConnectionException;

  T delete(T entry) throws RepositoryException, ConnectionException;

  List<T> query(Specification<T> specification) throws RepositoryException, ConnectionException;
}
