package li.selman.ddd.rest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

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

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
      return !request.getRequestURI().startsWith("/actuator/prometheus");
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
