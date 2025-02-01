package li.selman.ddd.common.infra.httpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class HttpClientConfiguration {

    /**
     *
     * @param ip
     * @return a dedicated Req
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ClientLoggerRequestInterceptor clientLoggerRequestInterceptor(InjectionPoint ip) {
        Logger logger = LoggerFactory.getLogger(ip.getDeclaredType());
        return new ClientLoggerRequestInterceptor(logger);
    }

}
