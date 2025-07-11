package li.selman.ddd.statement.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import li.selman.ddd.AbstractPropertyTest;
import li.selman.ddd.common.MyHttpClientProperties;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class PostHttpClientPropertiesTest extends AbstractPropertyTest {

    @Test
    void valid() throws JsonProcessingException {
        var properties = mapper.readValue(
                """
                        my:
                            clients:
                              post:
                                base-url: "https://jsonplaceholder.typicode.com"
                                connection-timeout: 1s
                        """, MyHttpClientProperties.class);

        assertThat(validator.validate(properties)).isEmpty();
    }

    @EnableConfigurationProperties(MyHttpClientProperties.class)
    static class TestConfiguration {
    }

    /**
     * Test using YAML
     */
    @Nested
    @SpringBootTest(classes = {TestConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
    @ActiveProfiles("properties-ittest")
    class ITTest {

        @Autowired
        MyHttpClientProperties properties;

        @Test
        void valid() {
            assertThat(properties.forClient("post").httpReadTimeout()).isEqualTo(Duration.of(1, ChronoUnit.SECONDS));
        }
    }

    /**
     * Test using properties format
     */
    @Nested
    class MyHttpClientPropertiesContextRunnerTest {

        private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                .withUserConfiguration(TestConfiguration.class)
                .withPropertyValues(
                        "my.clients.post.base-url=https://api.service-a.com",
                        "my.clients.post.http-read-timeout=5s",
                        "my.clients.post.http-write-timeout=3s"
                );

        @Test
        void shouldBindConfigurationProperties() {
            contextRunner.run(context -> {
                MyHttpClientProperties properties = context.getBean(MyHttpClientProperties.class);
                var config = properties.forClient("post");

                assertThat(config.baseUrl()).isEqualTo("https://api.service-a.com");
                assertThat(config.httpReadTimeout()).isEqualTo(Duration.ofSeconds(5));
                assertThat(config.httpWriteTimeout()).isEqualTo(Duration.ofSeconds(3));
            });
        }
    }
}
