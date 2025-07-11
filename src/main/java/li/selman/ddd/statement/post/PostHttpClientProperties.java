package li.selman.ddd.statement.post;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "my.http.client.post")
class PostHttpClientProperties {
//        extends MyHttpClientProperties {

//    @ConstructorBinding
//    public PostHttpClientProperties(
//            String baseUrl,
//            @DefaultValue("1000ms") Duration connectionTimeout,
//            @DefaultValue("5s") Duration requestTimeout) {
//        super(baseUrl, connectionTimeout, requestTimeout);
//    }
}
