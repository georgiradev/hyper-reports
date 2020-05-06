package hyper.reports.database.service.card;

import hyper.reports.database.repository.CardDetailsRepository;
import hyper.reports.database.repository.specification.card.FindByNumberAndTypeSpecification;
import hyper.reports.database.repository.specification.card.FindCardById;
import hyper.reports.entity.CardDetails;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.RepositoryException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public class CardDetailServiceImpl implements CardDetailService<CardDetails> {

  private CardDetailsRepository<CardDetails> cardDetailsRepository;

  public CardDetailServiceImpl() {
    this.cardDetailsRepository = new CardDetailsRepository<>();
  }

  @Override
  public Optional<CardDetails> findIsPresent(CardDetails cardDetails)
      throws ConnectionException, RepositoryException {
    List<CardDetails> list =
        cardDetailsRepository.query(
            new FindByNumberAndTypeSpecification(
                cardDetails.getNumber(),
                cardDetails.getCardType(),
                String.valueOf(cardDetails.isContactLess())));

    if (list != null && list.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(list.get(0));
    }
  }

  @Override
  public CardDetails insert(CardDetails entity) throws RepositoryException, ConnectionException {
    Optional<CardDetails> cardDetailsOptional = findIsPresent(entity);
    if (cardDetailsOptional.isPresent()) {
      return cardDetailsOptional.get();
    } else {
      return cardDetailsRepository.insert(entity);
    }
  }

  @Override
  public CardDetails update(CardDetails entity) throws RepositoryException, ConnectionException {
    return cardDetailsRepository.update(entity);
  }

  @Override
  public CardDetails delete(CardDetails entity) throws RepositoryException, ConnectionException {
    return cardDetailsRepository.delete(entity);
  }

  @Override
  public Optional<CardDetails> findByID(int cardId)
      throws ConnectionException, RepositoryException {
    List<CardDetails> list = cardDetailsRepository.query(new FindCardById(cardId));
    if (list.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(list.get(0));
    }
  }
}
