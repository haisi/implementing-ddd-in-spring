package li.selman.ddd.statement.infrastructure.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import li.selman.ddd.statement.Statement.StatementId;
import org.springframework.lang.Nullable;

class StatementIdSerializer extends StdSerializer<StatementId> {

    protected StatementIdSerializer() {
        super(StatementId.class);
    }

    @Override
    public void serialize(
            @Nullable StatementId statementId, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        if (statementId != null) {
            jsonGenerator.writeString(statementId.toBusinessId());
        } else {
            jsonGenerator.writeNull();
        }
    }
}
