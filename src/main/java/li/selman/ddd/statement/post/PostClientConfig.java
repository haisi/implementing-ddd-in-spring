package li.selman.ddd.statement.post;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
class PostClientConfig {

  @Bean
  PostClient postClient(
      RestClient.Builder restClientBuilder, PostHttpClientProperties postHttpClientProperties) {
    // Docs:
    // https://docs.spring.io/spring-boot/reference/io/rest-client.html#io.rest-client.restclient
    var requestFactory = new HttpComponentsClientHttpRequestFactory();
    requestFactory.setConnectTimeout(postHttpClientProperties.getConnectTimeout());
    requestFactory.setConnectionRequestTimeout(postHttpClientProperties.getRequestTimeout());

    RestClient restClient =
        restClientBuilder
            .baseUrl(postHttpClientProperties.getBaseUrl().toString())
            .requestFactory(requestFactory)
            .build();
    HttpServiceProxyFactory factory =
        HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
    return factory.createClient(PostClient.class);
  }
}
