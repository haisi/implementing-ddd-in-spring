package li.selman.ddd.statement.infrastructure.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import li.selman.ddd.statement.Statement;
import org.springframework.stereotype.Component;

@Component
class StatementJacksonModule extends SimpleModule {

    public StatementJacksonModule() {
        addSerializer(Statement.StatementId.class, new StatementIdSerializer());
        addValueInstantiator(Statement.StatementId.class, new StatementIdInstantiator());
    }
}
