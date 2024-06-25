package li.selman.ddd.statement;

import li.selman.ddd.common.DeepCopyAggregateForInMemoryRepository;
import org.jmolecules.ddd.types.AggregateRoot;
import org.jmolecules.ddd.types.Identifier;

import java.util.List;
import java.util.Optional;

public class InMemoryStatementRepository implements StatementRepository, DeepCopyAggregateForInMemoryRepository {
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
        return null;
    }

    @Override
    public Statement.StatementId nextIdentifier() {
        return null;
    }

    @Override
    public <T extends AggregateRoot<T, ID>, ID extends Identifier> T deepCopy(T object) {
        return null;
    }
}
