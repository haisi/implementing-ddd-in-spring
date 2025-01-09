package li.selman.ddd.statement.infrastructure.persistence;

import java.time.LocalDate;
import java.time.ZoneId;
import li.selman.ddd.statement.NextIdentifier;
import li.selman.ddd.statement.Statement;

public class NextIdentifierImpl implements NextIdentifier {
    @Override
    public Statement.StatementId nextIdentifier() {
        return new Statement.StatementId(LocalDate.now(ZoneId.systemDefault()), Statement.Source.TWITTER, 1);
    }
}
