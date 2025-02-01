package li.selman.ddd.common.infra.httpclient;

import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Tests whether:
 * <ul>
 *     <li>Logging works</li>
 *     <li>in-process retry works</li>
 *     <li>Exception wrapping works</li>
 *     <li>Observability</li>
 *     <li>Tracing</li>
 *     <li>Metrics</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockRestServiceServer
class MyRestClientCustomizerTest {

    @Autowired
    private MockRestServiceServer server;

    /**
     * Either directly use the RestClient with {@code restClientBuilder.build()}
     */
    @Autowired
    RestClient.Builder restClientBuilder;

    /**
     * Or use Feign approach
     */
    private TestClient client;

    @BeforeEach
    void setUp() {
        server = MockRestServiceServer.bindTo(restClientBuilder).build(); // make the myClient use the mock

        RestClient restClient = restClientBuilder.build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory //
                .builderFor(RestClientAdapter.create(restClient)) //
                .build();
        client = factory.createClient(TestClient.class);
    }

    @Test
    void test() {
        @Language("JSON")
        String postJson =
                """
                  {
                    "author": "test",
                    "value": "Mein Kommentar"
                  }
                  """;
        server.expect(requestTo("/comments")).andRespond(withSuccess(postJson, APPLICATION_JSON));

        // when then
        TestClient.Comment body = client.findAll();
//        var body = restClientBuilder.build().get().uri("/comments").retrieve().body(TestClient.Comment.class);

        System.out.println(body);
    }
}

interface TestClient {
    @GetExchange("/comments")
    Comment findAll();

    record Comment(String author, String value) {}
}
