package li.selman.ddd.statement;

import java.util.List;
import java.util.Optional;
import li.selman.ddd.statement.Statement.State;
import li.selman.ddd.statement.Statement.StatementId;
import org.jmolecules.ddd.types.Repository;

public interface StatementRepository
    extends Repository<Statement, StatementId>, NextIdentifier, CustomizedSave<Statement> {

  Optional<Statement> findById(StatementId id);

  List<String> findByState(State state);
}
