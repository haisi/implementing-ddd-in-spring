package li.selman.ddd.rest.statement;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import li.selman.ddd.common.AbstractWebIntegrationTest;
import li.selman.ddd.statement.InMemoryStatementRepository;
import li.selman.ddd.statement.Statement;
import li.selman.ddd.statement.StatementFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.MediaTypes;

class StatementControllerIntegrationTest extends AbstractWebIntegrationTest {

  @TestConfiguration
  static class TestContextConfiguration {
    @Bean
    InMemoryStatementRepository statementRepository() {
      return new InMemoryStatementRepository();
    }
  }

  @Autowired private InMemoryStatementRepository statementRepository;

  @Test
  void react() throws Exception {
    // given
    Statement statement = StatementFixture.aStatement().build();
    statementRepository.put(statement.getId(), statement);

    mvc.perform(get("/statement/" + statement.getId()))
        .andExpect(status().isOk()) //
        .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON)) //
        .andExpect(jsonPath("$._links.restbucks:orders.href", notNullValue()));
  }
}
