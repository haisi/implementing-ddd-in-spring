package li.selman.ddd.rest.statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import li.selman.ddd.common.AbstractWebIntegrationTest;
import li.selman.ddd.statement.InMemoryStatementRepository;
import li.selman.ddd.statement.Statement;
import li.selman.ddd.statement.StatementFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

@Import(StatementModelProcessor.class)
class StatementControllerIntegrationTest extends AbstractWebIntegrationTest {

    @TestConfiguration
    static class TestContextConfiguration {
        @Bean
        InMemoryStatementRepository statementRepository() {
            return new InMemoryStatementRepository();
        }
    }

    @Autowired
    private InMemoryStatementRepository statementRepository;

    @Nested
    @Import(TestContextConfiguration.class)
    class GetAllStatements {
        @Test
        @DisplayName("GET /statement/{existing-resource} => 200")
        void resourceFound() throws Exception {
            // given
            Statement statement = StatementFixture.aStatement().build();
            statementRepository.put(statement.getId(), statement);

            mvc.perform(get("/statement/" + statement.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
                    .andExpect(jsonPath("$.id", is(statement.getId().toBusinessId())));
        }

        @Test
        @DisplayName("GET /statement/{missing-resource} => 404")
        void resourceNotFound() throws Exception {
            // given - no statements
            assertThat(statementRepository).isEmpty();

            // when - any statement is queried
            mvc.perform(get("/statement/" + StatementFixture.STATEMENT_ID))
                    // then - 404
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.status", is(404)))
                    .andDo(document(
                            "statement",
                            responseFields( // todo extract common Problem Details into List
                                    fieldWithPath("status")
                                            .type(JsonFieldType.NUMBER)
                                            .description("HTTP Verb"),
                                    fieldWithPath("type")
                                            .type(JsonFieldType.STRING)
                                            .description(""),
                                    fieldWithPath("title")
                                            .type(JsonFieldType.STRING)
                                            .description(""),
                                    fieldWithPath("detail")
                                            .type(JsonFieldType.STRING)
                                            .description(""),
                                    fieldWithPath("instance")
                                            .type(JsonFieldType.STRING)
                                            .description("The called endpoint"),
                                    fieldWithPath("errorCode")
                                            .type(JsonFieldType.STRING)
                                            .description(""), // TODO why string?
                                    fieldWithPath("timestamp")
                                            .type(JsonFieldType.STRING)
                                            .description(""))));
        }
    }
}
