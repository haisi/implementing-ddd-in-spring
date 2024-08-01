package li.selman.ddd.statement.post;

import li.selman.ddd.common.MyHttpClientProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.net.URL;
import java.time.Duration;

@ConfigurationProperties(prefix = "my.http.client.post")
class PostHttpClientProperties extends MyHttpClientProperties {

  public PostHttpClientProperties(
      URL baseUrl,
      @DefaultValue("1000ms") Duration connectionTimeout,
      @DefaultValue("5s") Duration requestTimeout) {
    super(baseUrl, connectionTimeout, requestTimeout);
  }
}
