package li.selman.ddd.statement.infrastructure.json;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import li.selman.ddd.statement.Statement;

public class StatementIdInstantiator extends ValueInstantiator {
    @Override
    public String getValueTypeDesc() {
        return Statement.StatementId.class.toString();
    }

    @Override
    public boolean canCreateFromString() {
        return true;
    }

    @Override
    public Object createFromString(DeserializationContext context, String value) {
        return Statement.StatementId.fromString(value);
    }
}
