package li.selman.ddd.rest.statement;

import li.selman.ddd.rest.ResourceNotFoundException;
import li.selman.ddd.statement.Statement;
import li.selman.ddd.statement.StatementRepository;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
class StatementController {

  private final StatementRepository statementsRepo;

  StatementController(StatementRepository statementRepository) {
    this.statementsRepo = statementRepository;
  }

  @GetMapping("/statement/{statementId}")
  HttpEntity<EntityModel<Statement>> react(@PathVariable Statement.StatementId statementId) {
    Statement statement =
        statementsRepo
            .findById(statementId)
            .orElseThrow(() -> new ResourceNotFoundException("Statement not found"));
    return ResponseEntity.ok(EntityModel.of(statement));
  }

  @PostMapping("/{statementId}/react")
  HttpEntity<?> react(
      @PathVariable Statement.StatementId statementId, @RequestParam String reaction) {

    //        statementsRepo.findById(statementId).orElseThrow(() -> new
    // ResponseStatusException(HttpStatusCode.valueOf(404)));

    return ResponseEntity.ok().build();
  }
}
