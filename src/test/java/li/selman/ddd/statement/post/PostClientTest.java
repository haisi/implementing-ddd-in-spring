package li.selman.ddd.statement.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestTemplateAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(
    value = PostClient.class,
    properties = "logging.level.org.apache.hc.client5.http.wire=DEBUG")
class PostClientTest {

  private static final String SERVICE_URL = "https://jsonplaceholder.typicode.com";

  @Autowired private RestTemplateBuilder restTemplateBuilder;
  private PostClient client;
  @Autowired private MockRestServiceServer server;
  @Autowired private ObjectMapper objectMapper;

  @Nested
  class FindById {

    @BeforeEach
    public void setUp() {
      if (client == null) {
        // Testing interface based clients is currently cumbersome
        // https://github.com/spring-projects/spring-boot/issues/31337
        // https://github.com/spring-projects/spring-boot/issues/39521
        RestTemplate restTemplate = restTemplateBuilder.build();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(SERVICE_URL));
        client =
            HttpServiceProxyFactory.builderFor(RestTemplateAdapter.create(restTemplate))
                .build()
                .createClient(PostClient.class);
        server = MockRestServiceServer.bindTo(restTemplate).build();
      }
    }

    @Test
    void shouldFindById() {
      @Language("JSON")
      String postJson =
          """
                      {
                        "userId": 42,
                        "id": 5,
                        "title": "qui est esse",
                        "body": "est rerum tempore vitae\\nsequi sint nihil reprehenderit dolor beatae ea dolores neque"
                      }
                      """;

      server
          .expect(requestTo("https://jsonplaceholder.typicode.com/posts/1"))
          .andRespond(withSuccess(postJson, APPLICATION_JSON));

      Post post = client.findById(1);

      assertThat(post.userId()).isEqualTo(42);
      assertThat(post.id()).isEqualTo(5);
      assertThat(post.title()).isEqualTo("qui est esse");
      assertThat(post.body())
          .isEqualTo(
              "est rerum tempore vitae\nsequi sint nihil reprehenderit dolor beatae ea dolores neque");
    }

    @Test
    void shouldNotFindById() {
      @Language("JSON")
      String emptyResponse = "{}";

      server
          .expect(requestTo("https://jsonplaceholder.typicode.com/posts/999"))
          .andRespond(withResourceNotFound().body(emptyResponse).contentType(APPLICATION_JSON));

      var ex = assertThrows(HttpClientErrorException.class, () -> client.findById(999));

      assertThat(ex.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(404));
      assertThat(ex.getResponseBodyAsString()).isEqualTo("{}");
      assertThat(ex.getMessage()).startsWith("404 Not Found");
    }
  }
}
