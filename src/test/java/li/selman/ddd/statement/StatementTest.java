package li.selman.ddd.statement;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class StatementTest {

  @Test
  public void equalsContract() {
    EqualsVerifier.simple().forClass(Statement.class).verify();
  }
}
