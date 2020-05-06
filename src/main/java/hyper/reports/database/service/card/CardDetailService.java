package hyper.reports.database.service.card;

import hyper.reports.entity.CardDetails;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.RepositoryException;

import java.util.Optional;

public interface CardDetailService<T> {
  Optional<CardDetails> findIsPresent(CardDetails cardDetails)
      throws RepositoryException, ConnectionException;

  CardDetails insert(CardDetails entity) throws RepositoryException, ConnectionException;

  CardDetails update(CardDetails entity) throws RepositoryException, ConnectionException;

  CardDetails delete(CardDetails entity) throws RepositoryException, ConnectionException;

  Optional<CardDetails> findByID(int cardId) throws ConnectionException, RepositoryException;
}
