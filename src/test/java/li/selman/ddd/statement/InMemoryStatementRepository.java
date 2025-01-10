package li.selman.ddd.statement;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import li.selman.ddd.common.DeepCopyAggregateForInMemoryRepository;
import org.jmolecules.ddd.types.AggregateRoot;
import org.jmolecules.ddd.types.Identifier;

public class InMemoryStatementRepository extends HashMap<Statement.StatementId, Statement>
        implements StatementRepository, DeepCopyAggregateForInMemoryRepository {
    @Override
    public Optional<Statement> findById(Statement.StatementId id) {
        Statement statement = get(id);
        return Optional.ofNullable(statement);
    }

    @Override
    public List<String> findByState(Statement.State state) {
        return List.of();
    }

    @Override
    public <S extends Statement> S save(S entity) {
        return entity;
    }

    @Override
    public Statement.StatementId nextIdentifier() {
        return StatementFixture.STATEMENT_ID;
    }

    @Override
    public <T extends AggregateRoot<T, ID>, ID extends Identifier> T deepCopy(T object) {
        return object;
    }
}
