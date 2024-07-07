package li.selman.ddd.statement.infrastructure.json;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import li.selman.ddd.statement.Statement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class JacksonModuleStatement {

  @Bean
  Module statementModule() {
    return new StatementModule();
  }


  static class StatementModule extends SimpleModule {
    public StatementModule() {
      addSerializer(Statement.StatementId.class, new StatementIdSerializer());
      addValueInstantiator(Statement.StatementId.class, new StatementIdInstantiator());
    }
  }
}
