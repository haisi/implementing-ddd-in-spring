package li.selman.ddd.statement;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Version;
import li.selman.ddd.statement.Reaction.ReactionId;
import org.jmolecules.ddd.types.AggregateRoot;
import org.jmolecules.ddd.types.Identifier;
import org.jmolecules.event.types.DomainEvent;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Statement
        extends AbstractAggregateRoot<Statement>
        implements AggregateRoot<Statement, Statement.StatementId> {

    @AttributeOverride(name = "id", column = @Column(name = "id"))
    private StatementId id;

    @Version
    private Long version;

    private State state;

    private List<Reaction> reactions = new ArrayList<>();

    Statement(StatementId id) {
        this.id = Objects.requireNonNull(id);
        this.registerEvent(new StatementCreated(id));
    }

    void reacted(Reaction reaction) {
        this.reactions.add(Objects.requireNonNull(reaction));
        this.registerEvent(new ReactionAdded(id, reaction.getId()));
    }

    @Override
    public StatementId getId() {
        return id;
    }

    public List<Reaction> getReactions() {
        return Collections.unmodifiableList(reactions);
    }

    public record StatementId(LocalDate date, Type type, Integer sequenceOfDay) implements Identifier {

        public static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("yyyyMMdd");

        public String toBusinessId() {
            String dateFormatted = date.format(DATE_PATTERN);
            String typeCode = type.code;
            return "%s-%s-%d".formatted(dateFormatted, typeCode, sequenceOfDay);
        }
    }

    public record StatementCreated(StatementId statementId) implements DomainEvent {}

    public record ReactionAdded(StatementId statementId, ReactionId reactionId) implements DomainEvent {}

    public enum State {
        OPEN, CLOSED
    }

    public enum Type {

        HELPFUL("HP"), PROVOCATION("PC");

        final String code;

        Type(String code) {
            this.code = code;
        }
    }
}
