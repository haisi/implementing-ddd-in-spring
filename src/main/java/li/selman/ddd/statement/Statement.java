package li.selman.ddd.statement;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Version;
import org.jmolecules.ddd.types.AggregateRoot;
import org.jmolecules.ddd.types.Identifier;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Statement
        extends AbstractAggregateRoot<Statement>
        implements AggregateRoot<Statement, Statement.StatementId> {

    @AttributeOverride(name = "id", column = @Column(name = "id"))
    private StatementId id;

    @Version
    private Long version;

    private State state;

    private List<Reaction> reactions = new ArrayList<>();

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
