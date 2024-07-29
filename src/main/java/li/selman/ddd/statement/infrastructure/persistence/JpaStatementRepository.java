package li.selman.ddd.statement.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import li.selman.ddd.common.StaleStateException;
import li.selman.ddd.statement.Statement;
import li.selman.ddd.statement.StatementRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

@Component
class JpaStatementRepository implements StatementRepository {

  private final SpringDataBookRepository springDataRepo;

  JpaStatementRepository(SpringDataBookRepository springDataRepo) {
    this.springDataRepo = springDataRepo;
  }

  @Override
  public Optional<Statement> findById(Statement.StatementId id) {
    return Optional.empty();
  }

  @Override
  public List<String> findByState(Statement.State state) {
    return List.of();
  }

  @Override
  public <S extends Statement> S save(S entity) {
    try {
      return springDataRepo.save(entity);
    } catch (OptimisticLockingFailureException ex) {
      throw StaleStateException.forAggregate(entity.getId());
    }
    // TODO retry on constraint violation for Id
  }

  @Override
  public Statement.StatementId nextIdentifier() {
    return null;
  }
}

interface StatementIdProjection {
  String getId();

}

interface SpringDataBookRepository extends JpaRepository<Statement, Statement.StatementId> {
  @Query()
  Optional<StatementIdProjection> findByAuthorIdAndState(
      Statement.AuthorId authorId, Statement.State state);
}
