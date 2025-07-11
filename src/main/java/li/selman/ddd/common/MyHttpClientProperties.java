package li.selman.ddd.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.Map;

@Validated
@ConfigurationProperties("my")
public class MyHttpClientProperties {

    private final Map<String, HttpClientConfig> clients;

    @ConstructorBinding
    public MyHttpClientProperties(Map<String, HttpClientConfig> clients) {
        this.clients = clients;
    }

    public HttpClientConfig forClient(String client) {
        return this.clients.get(client);
    }

    /**
     *
     * @param baseUrl
     * @param httpReadTimeout
     * @param httpWriteTimeout
     */
    public record HttpClientConfig(String baseUrl, @DefaultValue("1000ms") Duration httpReadTimeout, @DefaultValue("5s") Duration httpWriteTimeout) {
    }
}

// @ConfigurationProperties(prefix = "my.http.client")
//@Validated
//public class MyHttpClientProperties {
//
//    private final @NotNull String baseUrl;
//
//    /**
//     * Maximum duration client is willing to wait for a successful TCP three-way handshake to
//     * establish a reliable connection.
//     *
//     * <p>Usually determined by network quality.
//     */
//    private final @NotNull Duration connectTimeout;
//
//    /**
//     * Maximum duration client is willing to wait for a response from the server after a successful
//     * connection has been established.
//     *
//     * <p>Usually determined by server speed.
//     */
//    private final @NotNull Duration requestTimeout;
//
//    @ConstructorBinding
//    public MyHttpClientProperties(
//            String baseUrl,
//            @DefaultValue("1000ms") Duration connectionTimeout,
//            @DefaultValue("5s") Duration requestTimeout) {
//        this.baseUrl = baseUrl;
//        this.connectTimeout = connectionTimeout;
//        this.requestTimeout = requestTimeout;
//    }
//
//    public @NotNull String getBaseUrl() {
//        return baseUrl;
//    }
//
//    public @NotNull Duration getConnectTimeout() {
//        return connectTimeout;
//    }
//
//    public @NotNull Duration getRequestTimeout() {
//        return requestTimeout;
//    }
//}
