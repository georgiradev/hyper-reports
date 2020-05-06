package hyper.reports.database.repository.specification;

import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.DbConfigException;
import hyper.reports.exception.RepositoryException;

public interface Specification<T> {

    QueryInfo toQueryInfo() throws RepositoryException, ConnectionException;
}

