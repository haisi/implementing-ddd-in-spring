package li.selman.ddd.statement;

import org.jmolecules.ddd.types.Entity;
import org.jmolecules.ddd.types.Identifier;

public class Reaction implements Entity<Statement, Reaction.ReactionId> {

    private ReactionId id;

    @Override
    public ReactionId getId() {
        return id;
    }

    public record ReactionId(String value) implements Identifier {}
}
