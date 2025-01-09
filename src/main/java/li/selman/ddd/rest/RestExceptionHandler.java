package li.selman.ddd.rest;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    private final Tracer tracer;

    RestExceptionHandler(Tracer tracer) {
        this.tracer = tracer;
    }

    private void addAdditionalDetails(ProblemDetail problemDetail) {
        problemDetail.setProperty("timestamp", Instant.now());
        addTraceInfos(problemDetail);
    }

    private void addTraceInfos(ProblemDetail problemDetail) {
        Span span = tracer.currentSpan();
        if (span != null) {
            problemDetail.setProperty("traceId", span.context().traceId());
            problemDetail.setProperty("spanId", span.context().spanId());
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Springs default exception handlers already create ProblemDetails. Here, we extend Springs
     * ProblemDetails with additional details such as timestamps and trace ids.
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        ResponseEntity<Object> objectResponseEntity =
                super.handleExceptionInternal(ex, body, headers, statusCode, request);
        if (objectResponseEntity != null && objectResponseEntity.hasBody()) {
            if (objectResponseEntity.getBody() instanceof ProblemDetail) {
                addAdditionalDetails((ProblemDetail) objectResponseEntity.getBody());
            }
        }

        return objectResponseEntity;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex) {
        var problemDetail = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getMessage());
        problemDetail.setProperty("errorCode", "1");
        addAdditionalDetails(problemDetail);
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail otherwiseUnhandledException(HttpServletRequest request, Exception ex) {
        log.error(ex.getMessage(), ex.getCause());
        var problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setProperty("errorCode", "0");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setDetail(ex.getMessage());

        addAdditionalDetails(problemDetail);

        return problemDetail;
    }
}
