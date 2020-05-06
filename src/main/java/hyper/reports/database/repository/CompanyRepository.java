package hyper.reports.database.repository;

import hyper.reports.database.config.HikariCPDataSource;
import hyper.reports.database.repository.specification.QueryInfo;
import hyper.reports.database.repository.specification.Specification;
import hyper.reports.entity.Company;
import hyper.reports.exception.ConnectionException;
import hyper.reports.exception.RepositoryException;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static hyper.reports.entity.Tables.COMPANY;

@Slf4j
public class CompanyRepository<T> implements BaseRepository<Company> {

  public Company insert(Company entry) throws RepositoryException, ConnectionException {
    try (Connection connection = HikariCPDataSource.getConnection();
        DSLContext context = DSL.using(connection)) {

      context
          .insertInto(COMPANY)
          .columns(COMPANY.ID, COMPANY.NAME, COMPANY.ADDRESS)
          .values(entry.getUuid(), entry.getName(), entry.getAddress())
          .returningResult(COMPANY.ID)
          .fetchOne();

      log.info("Insert successful for entry {}", entry);

      return entry;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public Company update(Company entry) throws RepositoryException, ConnectionException {
    try (Connection connection = HikariCPDataSource.getConnection();
        DSLContext context = DSL.using(connection)) {

      context
          .update(COMPANY)
          .set(COMPANY.ADDRESS, entry.getAddress())
          .set(COMPANY.NAME, entry.getName())
          .where(COMPANY.ID.eq(entry.getUuid()))
          .execute();

      log.info("Update successful for entry {}", entry);

      return entry;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public Company delete(Company entry) throws RepositoryException, ConnectionException {
    try (Connection connection = HikariCPDataSource.getConnection();
        DSLContext create = DSL.using(connection)) {

      create.deleteFrom(COMPANY).where(COMPANY.ID.eq(entry.getUuid())).execute();

      log.info("Delete is successful for entry {}", entry);

      return entry;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public List<Company> query(Specification<Company> specification)
      throws RepositoryException, ConnectionException {
    List<Company> companies = new ArrayList<>();
    QueryInfo queryInfo = specification.toQueryInfo();
    String sql = queryInfo.getSql();
    Map<Integer, Object> placeholders = queryInfo.getPlaceholders();

    try (Connection connection = HikariCPDataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {

      for (Map.Entry<Integer, Object> entry : placeholders.entrySet()) {
        statement.setObject(entry.getKey(), entry.getValue());
      }
      try (ResultSet resultSet = statement.executeQuery()) {

        while (resultSet.next()) {
          Company company = new Company();
          int id = resultSet.getInt(1);
          String name = resultSet.getString(2);
          String address = resultSet.getString(3);
          company.setUuid(id);
          company.setName(name);
          company.setAddress(address);
          companies.add(company);
        }
      }

      return companies;

    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }
}
