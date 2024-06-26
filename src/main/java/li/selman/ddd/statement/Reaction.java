package li.selman.ddd.statement;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import org.jmolecules.ddd.types.Entity;
import org.jmolecules.ddd.types.Identifier;

@jakarta.persistence.Entity
public class Reaction implements Entity<Statement, Reaction.ReactionId> {

    @EmbeddedId
    @SuppressWarnings("NullAway.Init")
    private ReactionId id;

    protected Reaction() {
        // For JPA
    }

    public Reaction(ReactionId id) {
        this.id = id;
    }

    @Override
    public ReactionId getId() {
        return id;
    }

    public enum Type {
        VERBAL, COMMENT
    }

    @Embeddable
    public record ReactionId(String value) implements Identifier {
    }
}
