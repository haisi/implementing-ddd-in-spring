package li.selman.ddd.statement;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class StatementIdTest {

    @Test
    void toBusinessId() {
        // given
        var statementId = new Statement.StatementId(LocalDate.of(2024, 4, 30), Statement.Type.HELPFUL, 99);
        // then
        String businessId = statementId.toBusinessId();
        // then
        assertThat(businessId).isEqualTo("20240430-HP-99");
    }
}