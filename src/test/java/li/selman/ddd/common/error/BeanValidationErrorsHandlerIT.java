package li.selman.ddd.common.error;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.test.simple.SimpleTracer;
import li.selman.ddd.common.AbstractWebIntegrationTest;
import li.selman.ddd.statement.InMemoryStatementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.convention.TestBean;
import org.springframework.test.web.servlet.MockMvc;

// TODO create ONE documented IT test showing the structure of error response
//@IntegrationTest
//@AutoConfigureMockMvc
@Import(BeanValidationErrorsHandlerIT.TestContextConfiguration.class)
class BeanValidationErrorsHandlerIT extends AbstractWebIntegrationTest {

    @TestConfiguration
    static class TestContextConfiguration {

        @Bean
        Tracer tracer() {
            return Tracer.NOOP;
        }
    }

    @Autowired
    private MockMvc rest;

    @Test
    void shouldHandleBodyParameterValidationError() throws Exception {
        rest
                .perform(post("/api/bean-validation-errors/mandatory-body-parameter").content("{}").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.title").value("Bean validation error"))
                .andExpect(jsonPath("$.detail").value("One or more fields were invalid. See 'errors' for details."))
                .andExpect(jsonPath("$.errors.parameter").value("must not be blank"));
    }

    @Test
    void shouldHandleControllerParameterValidationError() throws Exception {
        rest
                .perform(get("/api/bean-validation-errors/complicated-path-variable/dummy"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.title").value("Bean validation error"))
                .andExpect(jsonPath("$.detail").value("One or more fields were invalid. See 'errors' for details."))
                .andExpect(jsonPath("$.errors.complicated").value("must match \"complicated\""));
    }
}
