package li.selman.ddd.common;

import org.jmolecules.ddd.types.Identifier;

public class StaleStateException extends RuntimeException {

  private StaleStateException(String id) {
    super(String.format("Aggregate of id %s is stale", id));
  }

  public static StaleStateException forAggregate(Identifier identifier) {
    return new StaleStateException(identifier.toString());
  }
}
