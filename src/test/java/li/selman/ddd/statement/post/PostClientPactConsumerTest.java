package li.selman.ddd.statement.post;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.MockServerConfig;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@RestClientTest(value = PostClient.class, properties = "logging.level.org.apache.hc.client5.http.wire=DEBUG")
@PactConsumerTest
@MockServerConfig(hostInterface = "localhost", port = "3456")
@TestPropertySource(properties = {
        "my.http.client.post.base-url=http://localhost:3456",
        "logging.level.au.com.dius.pact=DEBUG",
})
class PostClientPactConsumerTest {

    private final static String CONSUMER_SERVICE = "my-ddd-scs";
    // Obviously, this contract will never be verified
    private final static String PROVIDER_SERVICE = "json-placeholder";

    @Pact(consumer = CONSUMER_SERVICE, provider = PROVIDER_SERVICE)
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        return builder
                .given("test state")
                .uponReceiving("ExampleJavaConsumerPactRuleTest test interaction")
                .path("/")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body("{\"responsetest\": true}")
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "asdf")
    public void runTest() {
        // This will run against mockProvider
//        Map expectedResponse = new HashMap();
//        expectedResponse.put("responsetest", true);
//        assertThat(new ConsumerClient("http://localhost:8080").get("/"), expectedResponse);
        // set auth token
        // var result = assertDoesNotThrow(() -> client.doSomething);
        // assert result
        // snapshot result
    }

}
