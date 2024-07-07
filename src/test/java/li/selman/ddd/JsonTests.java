package li.selman.ddd;

import li.selman.ddd.statement.Statement;
import li.selman.ddd.statement.Statement.StatementId;
import li.selman.ddd.statement.infrastructure.json.JacksonModuleStatement;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.context.annotation.Import;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/** Tests whether JSON serialization and deserialization works as excepted. */
@JsonTest
@Import(JacksonModuleStatement.class)
class JsonTests {
  @Autowired private JacksonTester<StatementId> jacksonTester;

  // TODO test time

  @ParameterizedTest
  @ValueSource(
      strings = {
        "20240312-X-1",
        "19990101-YT-0",
        "19990101-YT-999999",
      })
  void serializeStatementId(String id) throws IOException {
    // given
    var statementId = StatementId.fromString(id);

    // when
    JsonContent<StatementId> json = jacksonTester.write(statementId);

    // then
    assertThat(json).extractingJsonPathStringValue("$").isEqualTo(id);
  }
}
