package li.selman.ddd.common.error;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class ApplicationErrorsConfiguration {

    @Bean("applicationErrorMessageSource")
    MessageSource applictionErrorMessageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();

        source.setBasename("classpath:/i18n/errors/application-errors");
        source.setDefaultEncoding("UTF-8");

        return source;
    }
}
