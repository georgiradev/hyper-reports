package hyper.reports.database.repository;

import hyper.reports.database.config.HikariCPDataSource;
import hyper.reports.database.repository.specification.QueryInfo;
import hyper.reports.database.repository.specification.Specification;
import hyper.reports.database.repository.specification.store.FindByNameAddressAndCompanyIdSpecification;
import hyper.reports.database.service.invoice.InvoiceService;
import hyper.reports.database.service.invoice.InvoiceServiceImpl;
import hyper.reports.database.service.receipt.ReceiptService;
import hyper.reports.database.service.receipt.ReceiptServiceImpl;
import hyper.reports.entity.Company;
import hyper.reports.entity.Invoice;
import hyper.reports.entity.Receipt;
import hyper.reports.entity.Store;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.RepositoryException;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.impl.DSL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static hyper.reports.entity.Tables.STORE;

@Slf4j
public class StoreRepository<T> implements BaseRepository<Store> {

  private ReceiptService<Receipt> receiptService;
  private InvoiceService<Invoice> invoiceService;

  public StoreRepository() {
    this.receiptService = new ReceiptServiceImpl();
    this.invoiceService = new InvoiceServiceImpl();
  }

  public void insertStoresInBatch(Company entry) throws RepositoryException, ConnectionException {
    try (Connection connection = HikariCPDataSource.getConnection();
        PreparedStatement preparedStatement =
            connection.prepareStatement(
                "INSERT INTO store(name, address, company_id) VALUES(?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {

      log.info(
          "Inserting data from {} stores in company {}", entry.getStores().size(), entry.getName());

      for (Store store : entry.getStores()) {
        Optional<Store> oneByNameAndAddress =
            getOneByNameAndAddress(store.getName(), store.getAddress(), entry.getUuid());
        if (oneByNameAndAddress.isEmpty()) {
          store.setCompanyId(entry.getUuid());
          preparedStatement.setString(1, store.getName());
          preparedStatement.setString(2, store.getAddress());
          preparedStatement.setInt(3, store.getCompanyId());
          preparedStatement.addBatch();
        } else {
          store.setUuid(oneByNameAndAddress.get().getUuid());
          store.setCompanyId(entry.getUuid());
        }
      }

      executeStoreBatch(preparedStatement, entry.getStores());

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  public void executeStoreBatch(PreparedStatement preparedStatement, List<Store> stores)
      throws ConnectionException, RepositoryException {
    try {
      preparedStatement.executeBatch();
      ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

      for (Store store : stores) {
        boolean next = generatedKeys.next();
        if (next) {
          store.setUuid(generatedKeys.getInt(1));
          log.info("Store {} is inserted to the database", store.getName());
        }
        receiptService.insertBatch(store);
        invoiceService.insertBatch(store);
      }
    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public Store update(Store entry) throws RepositoryException, ConnectionException {
    try (Connection connection = HikariCPDataSource.getConnection();
        DSLContext context = DSL.using(connection)) {
      Record1<Integer> record =
          context
              .update(STORE)
              .set(STORE.NAME, entry.getName())
              .set(STORE.ADDRESS, entry.getAddress())
              .returningResult(STORE.ID)
              .fetchOne();

      entry.setUuid(record.getValue(STORE.ID));
      log.info("Update successful for entry {}", entry);

      return entry;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public Store delete(Store entry) throws RepositoryException, ConnectionException {
    try (Connection connection = HikariCPDataSource.getConnection();
        DSLContext context = DSL.using(connection)) {

      context.deleteFrom(STORE).where(STORE.ID.eq(entry.getUuid()));

      log.info("Delete successful for entry {}", entry);

      return entry;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public List<Store> query(Specification<Store> specification)
      throws RepositoryException, ConnectionException {
    List<Store> stores = new ArrayList<>();
    QueryInfo queryInfo = specification.toQueryInfo();
    String sql = queryInfo.getSql();
    Map<Integer, Object> placeholders = queryInfo.getPlaceholders();

    try (Connection connection = HikariCPDataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {

      for (Map.Entry<Integer, Object> entry : placeholders.entrySet()) {
        statement.setObject(entry.getKey(), entry.getValue());
      }
      ResultSet resultSet = statement.executeQuery();

      while (resultSet.next()) {
        Store store = new Store();
        store.setUuid(resultSet.getInt(1));
        String name = resultSet.getString(2);
        String address = resultSet.getString(3);
        store.setName(name);
        store.setAddress(address);
        stores.add(store);
      }

      return stores;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  public Optional<Store> getOneByNameAndAddress(String name, String address, int companyId)
      throws RepositoryException, ConnectionException {
    List<Store> list =
        query(new FindByNameAddressAndCompanyIdSpecification(name, address, companyId));

    if (list.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(list.get(0));
    }
  }
}
