package li.selman.ddd.common.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;
import java.util.Optional;

//@ControllerAdvice
//@Order(Ordered.LOWEST_PRECEDENCE - 1_000)
public class ApplicationErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(ApplicationErrorHandler.class);

    static final String MESSAGE_PREFIX = "error.";
    static final String TITLE_SUFFIX = "title";
    static final String DETAIL_SUFFIX = "detail";

    private final MessageSource messages;

//    @Bean("applicationErrorMessageSource")
//    MessageSource applictionErrorMessageSource() {
//        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
//
//        source.setBasename("classpath:/i18n/errors/application-errors");
//        source.setDefaultEncoding("UTF-8");
//
//        return source;
//    }

    ApplicationErrorHandler(@Qualifier("applicationErrorMessageSource") MessageSource messages) {
        Locale.setDefault(Locale.ENGLISH);
        this.messages = messages;
    }

    @ExceptionHandler(MyException.class)
    ProblemDetail handleMyApplicationException(MyException exception) {
        HttpStatusCode status = Optional.ofNullable(exception.getStatusCode()).orElse(HttpStatus.INTERNAL_SERVER_ERROR);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, buildDetail(exception));

        problem.setTitle(getMessage(exception.getKey(), TITLE_SUFFIX));
        problem.setProperty("key", exception.getKey().get());

        logException(exception, status);

        return problem;
    }

    private String buildDetail(MyException exception) {
        String messageTemplate = getMessage(exception.getKey(), DETAIL_SUFFIX);
        return ArgumentsReplacer.replaceParameters(messageTemplate, exception.getParameters());
    }

    private String getMessage(ErrorKey key, String suffix) {
        return messages.getMessage(getMessageCode(key, suffix), null, locale());
    }

    static String getMessageCode(ErrorKey key, String suffix) {
        return MESSAGE_PREFIX + key.get() + "." + suffix;
    }

    private Locale locale() {
        return LocaleContextHolder.getLocale();
    }

    private void logException(MyException exception, HttpStatusCode status) {
        if (status.is4xxClientError()) {
            log.info(exception.getMessage(), exception);
        } else {
            log.error(exception.getMessage(), exception);
        }
    }
}
