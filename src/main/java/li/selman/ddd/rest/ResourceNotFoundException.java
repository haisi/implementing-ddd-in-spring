package li.selman.ddd.rest;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class ResourceNotFoundException extends ResponseStatusException {

  public ResourceNotFoundException(String reason) {
    super(HttpStatusCode.valueOf(404), reason);
  }
}
