package li.selman.ddd.rest;

import java.net.URI;
import java.time.Instant;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class RestExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

  @ExceptionHandler(ResourceNotFoundException.class)
  ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getMessage());
    problemDetail.setProperty("errorCode", "1");
    problemDetail.setProperty("timestamp", Instant.now());
    return problemDetail;
  }

  @ExceptionHandler(Exception.class)
  ProblemDetail otherwiseUnhandledException(HttpServletRequest request, Exception ex) {
    log.error(ex.getMessage(), ex.getCause());
    var problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    problemDetail.setProperty("errorCode", "0");
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    return problemDetail;
  }
}
