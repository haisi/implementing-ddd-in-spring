package li.selman.ddd.rest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import java.util.Set;

@Configuration
class RequestLoggingFilterConfig {
  @Bean
  public AbstractRequestLoggingFilter logFilter() {
    var filter = new MyCommonsRequestLoggingFilter();
    filter.setIncludeQueryString(true);
    filter.setIncludePayload(false);
    filter.setMaxPayloadLength(10000);
    filter.setIncludeHeaders(false);
    return filter;
  }

  static class MyCommonsRequestLoggingFilter extends AbstractRequestLoggingFilter {

    private static final Set<String> ENDPOINTS_NOT_TO_LOG =
        Set.of("/actuator/prometheus", "/actuator/health", "/actuator/info");

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
      // Certain endpoints get called regularly, e.g. every 5s, by other systems for operations
      // reasons. We do not want to log them, as they introduce too much noise.
      return ENDPOINTS_NOT_TO_LOG.stream().noneMatch(s -> request.getRequestURI().startsWith(s));
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
      logger.info(message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
      logger.info(message);
    }
  }
}
