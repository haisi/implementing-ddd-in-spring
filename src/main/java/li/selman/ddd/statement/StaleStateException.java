package li.selman.ddd.statement;

public class StaleStateException extends RuntimeException {

    private StaleStateException(String id) {
        super(String.format("Aggregate of id %s is stale", id));
    }

    public static StaleStateException forStatementWith(Statement.StatementId id) {
        return new StaleStateException(id.toBusinessId());
    }

}
