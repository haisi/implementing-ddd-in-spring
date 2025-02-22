package li.selman.ddd.common.error;

import org.springframework.http.HttpStatusCode;

import java.util.Map;

public class MyException extends RuntimeException {

    private final ErrorKey key;

    /**
     * The HTTP status code to return to client
     */
    private final HttpStatusCode statusCode;
    private final Map<String, String> parameters;

    MyException(ErrorKey key, HttpStatusCode statusCode, Map<String, String> parameters) {
        this.key = key;
        this.statusCode = statusCode;
        this.parameters = parameters;
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
