package li.selman.ddd.statement;

import jakarta.persistence.*;
import li.selman.ddd.statement.Reaction.ReactionId;
import org.jmolecules.ddd.types.AggregateRoot;
import org.jmolecules.ddd.types.Identifier;
import org.jmolecules.ddd.types.ValueObject;
import org.jmolecules.event.types.DomainEvent;
import org.jmolecules.jpa.JMoleculesJpa;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 */
@SuppressWarnings("NullAway.Init")
@Entity
public class Statement
        extends AbstractAggregateRoot<Statement>
        implements AggregateRoot<Statement, Statement.StatementId> {

    @EmbeddedId
    @AttributeOverride(name = "date", column = @Column(name = "made_at"))
    @AttributeOverride(name = "type", column = @Column(name = "type"))
    @AttributeOverride(name = "sequenceOfDay", column = @Column(name = "sequence_of_day"))
    private StatementId id;

    @Version
    @SuppressWarnings("unused")
    private Long version;

    @Enumerated(EnumType.STRING)
    private State state;

    @AttributeOverride(name = "value", column = @Column(name = "author_id"))
    private AuthorId authorId;

    @OneToMany(
            fetch = FetchType.EAGER,
            orphanRemoval = true,
            cascade = {CascadeType.ALL},
            mappedBy = ""
    )
    private List<Reaction> reactions = new ArrayList<>();

    protected Statement() {
        // Make JPA happy
    }

    Statement(StatementId id) {
        this.id = Objects.requireNonNull(id);
        this.registerEvent(new StatementCreated(id));
    }

    @PostLoad
    void postLoad() {
        // In case someone did something funny in the database
        JMoleculesJpa.verifyNullability(this);
    }

    @PrePersist
    void prePersist() {
        // In case someone did something funny with reflection --- the rest is caught by nullaway
        JMoleculesJpa.verifyNullability(this);
    }

    void reacted(Reaction reaction) {
        // TODO only allow if state is OPEN
        if (!State.OPEN.equals(state)) {
            throw new IllegalArgumentException();
        }
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

    @Embeddable
    public record StatementId(LocalDate date,
                              @Enumerated(EnumType.STRING) Type type,
                              Integer sequenceOfDay) implements Identifier {

        private static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("yyyyMMdd");

        public String toBusinessId() {
            String dateFormatted = date.format(DATE_PATTERN);
            String typeCode = type.code;
            return "%s-%s-%d".formatted(dateFormatted, typeCode, sequenceOfDay);
        }

        public static StatementId fromString(String string) {
            String[] split = string.split("-");
            if (split.length != 3) {
                throw new IllegalArgumentException("Not a valid statement id: " + string);
            }
            LocalDate date = LocalDate.parse(split[0], DATE_PATTERN);
            Type type = Type.fromCode(split[1]);
            Integer sequenceOfDay = Integer.valueOf(split[2]);

            return new StatementId(date, type, sequenceOfDay);
        }

        @Override
        public String toString() {
            return toBusinessId();
        }
    }

    public record StatementCreated(StatementId statementId) implements DomainEvent {
    }

    public record ReactionAdded(StatementId statementId, ReactionId reactionId) implements DomainEvent {
    }

    @Embeddable
    public record AuthorId(String value) implements ValueObject {
    }

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
