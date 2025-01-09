package li.selman.ddd;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import li.selman.ddd.statement.Statement.StatementId;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

/** Tests whether JSON serialization and deserialization works as excepted. */
@JsonTest
class JsonTests {
    @Autowired
    private JacksonTester<StatementId> jacksonTester;

    // TODO test time

    @ParameterizedTest
    @ValueSource(
            strings = {
                "20240312-X-1",
                "19990101-YT-0",
                "19990101-YT-999999",
            })
    void serializeStatementId(String id) throws IOException {
        // given - a Java StatementId Object containing multiple fields
        var statementId = StatementId.fromString(id);
        // when - Jackson serializes the object
        JsonContent<StatementId> json = jacksonTester.write(statementId);
        // then - JSON is a single string
        assertThat(json).extractingJsonPathStringValue("$").isEqualTo(id);

        // given - JSON StatementId, i.e. a single string field
        String jsonString = json.getJson();
        // when - Jackson deserializes the JSON
        StatementId parsedStatementId = jacksonTester.parseObject(jsonString);
        // then - the created Java object is the same as at the beginning
        assertThat(parsedStatementId).isEqualTo(statementId);
    }
}
