package li.selman.ddd.rest.statement;

import li.selman.ddd.statement.Statement;
import li.selman.ddd.statement.StatementRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
class StatementController {

  private final StatementRepository statementsRepo;

  StatementController(StatementRepository statementRepository) {
    this.statementsRepo = statementRepository;
  }

  @GetMapping("/statement/{statementId}")
  HttpEntity<?> react(@PathVariable Statement.StatementId statementId) {
    Statement statement =
        statementsRepo
            .findById(statementId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404)));
    return ResponseEntity.ok(statement);
  }

  @PostMapping("/{statementId}/react")
  HttpEntity<?> react(
      @PathVariable Statement.StatementId statementId, @RequestParam String reaction) {

    //        statementsRepo.findById(statementId).orElseThrow(() -> new
    // ResponseStatusException(HttpStatusCode.valueOf(404)));

    return ResponseEntity.ok().build();
  }
}
