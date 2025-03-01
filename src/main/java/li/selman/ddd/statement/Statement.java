package li.selman.ddd.statement;

import jakarta.persistence.*;
import li.selman.ddd.fsm.Transition;
import li.selman.ddd.fsm.Transitionable;
import li.selman.ddd.statement.Reaction.ReactionId;
import org.hibernate.proxy.HibernateProxy;
import org.jmolecules.ddd.types.AggregateRoot;
import org.jmolecules.ddd.types.Identifier;
import org.jmolecules.ddd.types.ValueObject;
import org.jmolecules.event.types.DomainEvent;
import org.jmolecules.jpa.JMoleculesJpa;
import org.slf4j.Logger;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static li.selman.ddd.statement.Statement.State.*;
import static org.slf4j.LoggerFactory.getLogger;

@SuppressWarnings("NullAway.Init")
@Entity
public class Statement extends AbstractAggregateRoot<Statement>
        implements AggregateRoot<Statement, Statement.StatementId> {

    private static final Logger log = getLogger(Statement.class);

    @EmbeddedId
    @AttributeOverride(name = "madeAt", column = @Column(name = "made_at"))
    @AttributeOverride(name = "source", column = @Column(name = "source"))
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
            mappedBy = "")
    @JoinColumns({
        @JoinColumn(name = "madeAt", referencedColumnName = "made_at"),
        @JoinColumn(name = "source", referencedColumnName = "source"),
        @JoinColumn(name = "sequenceOfDay", referencedColumnName = "sequence_of_day")
    })
    private List<Reaction> reactions = new ArrayList<>();

    protected Statement() {
        // Make JPA happy
    }

    Statement(StatementId id) {
        this.id = Objects.requireNonNull(id);
        this.state = OPEN;
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
        if (!OPEN.equals(state)) {
            throw new IllegalArgumentException();
        }
        this.reactions.add(Objects.requireNonNull(reaction));
        this.registerEvent(new ReactionAdded(id, reaction.getId()));
    }

    void startReview() {
        log.info("Starting review");
        updateState(StateFsm.TO_IN_REVIEW);
    }

    private void updateState(StateFsm toState) {
        State prevState = this.state;
        this.state = toState.fromState(this.state);
        this.registerEvent(new StatementInReview(this.id));
        log.info("Changed state from '{}' to '{}'", prevState, state);
    }

    @Override
    public StatementId getId() {
        return id;
    }

    public State getState() {
        return state;
    }

    public List<Reaction> getReactions() {
        return Collections.unmodifiableList(reactions);
    }

    @Embeddable
    public record StatementId(LocalDate madeAt, @Enumerated(EnumType.STRING) Source source, Integer sequenceOfDay)
            implements Identifier {

        private static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("yyyyMMdd");

        public String toBusinessId() {
            String dateFormatted = madeAt.format(DATE_PATTERN);
            String typeCode = source.code;
            return "%s-%s-%d".formatted(dateFormatted, typeCode, sequenceOfDay);
        }

        public static StatementId fromString(String string) {
            String[] split = string.split("-");
            if (split.length != 3) {
                throw new IllegalArgumentException("Not a valid statement id: " + string);
            }
            LocalDate date = LocalDate.parse(split[0], DATE_PATTERN);
            Source source = Source.fromCode(split[1]);
            Integer sequenceOfDay = Integer.valueOf(split[2]);

            return new StatementId(date, source, sequenceOfDay);
        }

        @Override
        public String toString() {
            return toBusinessId();
        }
    }

    public record StatementCreated(StatementId statementId) implements DomainEvent {}

    public record ReactionAdded(StatementId statementId, ReactionId reactionId) implements DomainEvent {}

    public record StatementInReview(StatementId statementId) implements DomainEvent {}

    @Embeddable
    public record AuthorId(String value) implements ValueObject {}

    public enum State {
        OPEN,
        IN_REVIEW,
        CLOSED
    }

    public enum StateFsm implements Transitionable<State> {
        TO_IN_REVIEW(Transition.from(OPEN, IN_REVIEW).to(IN_REVIEW)),
        TO_CLOSED(Transition.from(OPEN, IN_REVIEW, CLOSED).to(CLOSED));

        @SuppressWarnings("ImmutableEnumChecker")
        private final Transition<State> transition;

        StateFsm(Transition<State> transition) {
            this.transition = transition;
        }

        @Override
        public Transition<State> getTransition() {
            return transition;
        }

        @Override
        public State fromState(State current) {
            return Objects.requireNonNull(this.transition.evaluateTransition(
                    current,
                    () -> new IllegalArgumentException("State transition from '%s' to '%s' in invalid"
                            .formatted(current, transition.getFinalState()))));
        }
    }

    public enum Source {
        TWITTER("X"),
        YOUTUBE("YT");

        final String code;

        Source(String code) {
            this.code = code;
        }

        public static Source fromCode(String code) {
            for (Source source : Source.values()) {
                if (source.code.equals(code)) {
                    return source;
                }
            }
            throw new IllegalArgumentException("Unknown statement source: " + code);
        }
    }

    @Override
    public String toString() {
        return "Statement{" + "authorId=" + authorId + ", state=" + state + ", version=" + version + ", id=" + id + '}';
    }

    @SuppressWarnings("EqualsGetClass")
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Statement statement = (Statement) o;
        return getId() != null && Objects.equals(getId(), statement.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this)
                        .getHibernateLazyInitializer()
                        .getPersistentClass()
                        .hashCode()
                : getClass().hashCode();
    }
}
