package li.selman.ddd.common;

import li.selman.ddd.common.error.MyException;
import li.selman.ddd.common.error.StandardErrorKey;
import li.selman.ddd.common.error.StandardTemporality;
import org.jmolecules.ddd.types.Identifier;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class StaleStateException extends MyException {

    private StaleStateException(String id) {
        super(
                String.format("Aggregate of id %s is stale", id),
                StandardErrorKey.PROCESS_CONFLICT,
                StandardTemporality.TEMPORARY_GLOBAL,
                HttpStatus.CONFLICT,
                Map.of("object-id", id));
    }

    public static StaleStateException forAggregate(Identifier identifier) {
        return new StaleStateException(identifier.toString());
    }
}
