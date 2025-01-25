package li.selman.ddd.common.infra.httpclient;

import com.google.errorprone.annotations.Var;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class MyRestClientCustomizer implements RestClientCustomizer {

    @Override
    public void customize(RestClient.Builder restClientBuilder) {
        // check wheterh ObservationRestClientCustomizer is called
        restClientBuilder.requestInterceptors(null);
    }


    // Theoretisch hätte die exception im RestController Adive alle nötigen sachen aus dem response.
    // Eigentlich könnte man alle Requests als info loggen und alle errors dann noch als ERROR
    // hmmm, dann brauch ich dass ganze eigentlich gar nicht mehr. Per default wird schon 404 mit
    // empty body gelogged
    @Bean
    RestClientCustomizer errorLogRestClientCustomizer() { // TODO test retry, logging, exception wrapping for Kafka and HTTP requests
        // org.springframework.web.client.DefaultResponseErrorHandler

        // Docs:
        // https://docs.spring.io/spring-boot/reference/io/rest-client.html#io.rest-client.restclient
        var requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setConnectionRequestTimeout(Duration.ofSeconds(5));

        return builder -> builder
                .requestInterceptor(null)
                .requestFactory(requestFactory)
                .defaultStatusHandler(
                        HttpStatusCode::isError, // make configurable, so that with flag, all requests all logged
                        // TODO create generic http client properties
                        (request, response) -> {
                            HttpMethod httpMethod = request.getMethod();
                            var uri = request.getURI();

                            // Only do, if we actually log it
                            byte[] body = getResponseBody(response);
                            @SuppressWarnings("UnusedVariable")
                            Charset charset = getCharset(response);

                            HttpStatusCode statusCode = response.getStatusCode();
                            String statusText = response.getStatusText();
                            var message = "HTTP request '%s %s' failed with '%s %s'"
                                    .formatted(httpMethod, uri, statusCode, statusText);

                            //                    String errorMessage = getErrorMessage(statusCode.value(),
                            // response.getStatusText(), body, charset);

                            if (statusCode.is4xxClientError()) {
                                throw new HttpClientErrorException(
                                        message, statusCode, statusText, response.getHeaders(), body, StandardCharsets.UTF_8);
                            } else if (statusCode.is5xxServerError()) {
                                throw new HttpServerErrorException(
                                        message, statusCode, statusText, response.getHeaders(), body, StandardCharsets.UTF_8);

                            } else {
                                throw new UnknownHttpStatusCodeException(
                                        message,
                                        statusCode.value(),
                                        statusText,
                                        response.getHeaders(),
                                        body,
                                        StandardCharsets.UTF_8);
                            }
                        });
    }

    @SuppressWarnings("UnusedMethod")
    private String getErrorMessage(
            int rawStatusCode, String statusText, @Nullable byte[] responseBody, @Var @Nullable Charset charset) {

        String preface = rawStatusCode + " " + statusText + ": ";

        if (ObjectUtils.isEmpty(responseBody)) {
            return preface + "[no body]";
        }

        charset = (charset != null ? charset : StandardCharsets.UTF_8);

        @Var String bodyText = new String(responseBody, charset);
        bodyText = LogFormatUtils.formatValue(bodyText, -1, true);

        return preface + bodyText;
    }

    @Nullable
    protected Charset getCharset(ClientHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        MediaType contentType = headers.getContentType();
        return (contentType != null ? contentType.getCharset() : null);
    }


    protected byte[] getResponseBody(ClientHttpResponse response) {
        try {
            return FileCopyUtils.copyToByteArray(response.getBody());
        } catch (IOException ex) {
            // ignore
        }
        return new byte[0];
    }
}
