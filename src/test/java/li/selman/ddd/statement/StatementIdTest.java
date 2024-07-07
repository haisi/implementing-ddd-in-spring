package li.selman.ddd.statement;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class StatementIdTest {

  @Test
  void toBusinessId() {
    // given
    var statementId =
        new Statement.StatementId(LocalDate.of(2024, 4, 30), Statement.Source.TWITTER, 99);
    // then
    String businessId = statementId.toBusinessId();
    // then
    assertThat(businessId).isEqualTo("20240430-X-99");
  }
}
