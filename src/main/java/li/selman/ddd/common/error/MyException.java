package li.selman.ddd.common.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.Map;

public class MyException extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(MyException.class);

    private final ErrorKey key;
    private final Temporality temporality;

    /**
     * The HTTP status code to return to client
     */
    private final HttpStatusCode statusCode;

    private final Map<String, String> parameters;

    MyException(
            String message,
            ErrorKey key,
            Temporality temporality,
            HttpStatusCode statusCode,
            Map<String, String> parameters) {
        super(message);
        this.key = key;
        this.temporality = temporality;
        this.statusCode = statusCode;
        this.parameters = parameters;
    }

    MyException(
            String message,
            Throwable cause,
            ErrorKey key,
            Temporality temporality,
            HttpStatusCode statusCode,
            Map<String, String> parameters) {
        super(message, cause);
        this.key = key;
        this.temporality = temporality;
        this.statusCode = statusCode;
        this.parameters = parameters;
    }

    /**
     * Turns any exception that is not an instance of {@link MyException} into a {@link MyException}.
     * The newly composed exception is always an internal server error (HTTP 500) and permanent.
     *
     * @param exception not instance of {@link MyException}
     * @return MyException composed of generic exception
     */
    public static MyException composeGeneric(Throwable exception) {
        if (exception instanceof MyException) {
            log.warn("Using MyException#generic wrongly. No need to wrap {} in MyException.", exception.getClass().getSimpleName());
            return (MyException) exception;
        }

        return new MyException(
                exception.getLocalizedMessage(),
                exception,
                determineErrorKeyForUnknownErrors(exception),
                StandardTemporality.PERMANENT,
                HttpStatus.INTERNAL_SERVER_ERROR,
                Map.of());
    }

    static ErrorKey determineErrorKeyForUnknownErrors(Throwable throwable) {
        if (throwable instanceof NullPointerException) {
            return StandardErrorKey.NULL_POINTER;
        } else if (throwable instanceof IllegalArgumentException) {
            return StandardErrorKey.ILLEGAL_ARGUMENT;
        }
        return StandardErrorKey.INTERNAL_SERVER_ERROR;
    }

    public ErrorKey getKey() {
        return key;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
