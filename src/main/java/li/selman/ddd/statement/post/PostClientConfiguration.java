package li.selman.ddd.statement.post;

import am.ik.spring.http.client.RetryableClientHttpRequestInterceptor;
import li.selman.ddd.common.infra.httpclient.ClientLoggerRequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
class PostClientConfiguration {

    private static final Logger log = LoggerFactory.getLogger(PostClientConfiguration.class);

    @Bean
    PostClient postClient(
            RestClient.Builder restClientBuilder,
//            PostHttpClientProperties postHttpClientProperties,
            ClientLoggerRequestInterceptor clientLoggerRequestInterceptor) {
        // Docs:
        // https://docs.spring.io/spring-boot/reference/io/rest-client.html#io.rest-client.restclient
        var requestFactory = new HttpComponentsClientHttpRequestFactory();
//        requestFactory.setConnectTimeout(postHttpClientProperties.getConnectTimeout());
//        requestFactory.setConnectionRequestTimeout(postHttpClientProperties.getRequestTimeout());

        restClientBuilder.requestInterceptors(clientHttpRequestInterceptors -> log.info(""));


        RestClient restClient = restClientBuilder
//                .baseUrl(postHttpClientProperties.getBaseUrl().toString())
                .requestInterceptor(clientLoggerRequestInterceptor)
                .requestInterceptor(new RetryableClientHttpRequestInterceptor(new FixedBackOff(100, 2)))
                .requestFactory(requestFactory)
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(PostClient.class);
    }
}
