package li.selman.ddd.common;

import jakarta.validation.constraints.NotNull;
import java.net.URL;
import java.time.Duration;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

// @ConfigurationProperties(prefix = "my.http.client")
@Validated
public class MyHttpClientProperties {

    private final @NotNull URL baseUrl;

    /**
     * Maximum duration client is willing to wait for a successful TCP three-way handshake to
     * establish a reliable connection.
     *
     * <p>Usually determined by network quality.
     */
    private final @NotNull Duration connectTimeout;

    /**
     * Maximum duration client is willing to wait for a response from the server after a successful
     * connection has been established.
     *
     * <p>Usually determined by server speed.
     */
    private final @NotNull Duration requestTimeout;

    @ConstructorBinding
    public MyHttpClientProperties(
            URL baseUrl,
            @DefaultValue("1000ms") Duration connectionTimeout,
            @DefaultValue("5s") Duration requestTimeout) {
        this.baseUrl = baseUrl;
        this.connectTimeout = connectionTimeout;
        this.requestTimeout = requestTimeout;
    }

    public @NotNull URL getBaseUrl() {
        return baseUrl;
    }

    public @NotNull Duration getConnectTimeout() {
        return connectTimeout;
    }

    public @NotNull Duration getRequestTimeout() {
        return requestTimeout;
    }
}
