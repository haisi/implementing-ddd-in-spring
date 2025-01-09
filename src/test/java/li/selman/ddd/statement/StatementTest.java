package li.selman.ddd.statement;

import li.selman.ddd.EqualsTester;
import org.junit.jupiter.api.Test;

class StatementTest {

    @Test
    public void equalsContract() {
        EqualsTester.testJpaEquals(Statement.class);
    }
}
