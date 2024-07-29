package li.selman.ddd.statement.post;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
class PostClientConfig {

  @Bean
  PostClient postClient() {
    var restClient = RestClient.create("https://jsonplaceholder.typicode.com");
    var factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
    return factory.createClient(PostClient.class);
  }
}
