package li.selman.ddd.rest.statement;

import li.selman.ddd.rest.ResourceNotFoundException;
import li.selman.ddd.statement.Statement;
import li.selman.ddd.statement.StatementRepository;
import li.selman.ddd.statement.post.Post;
import li.selman.ddd.statement.post.PostClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
class StatementController {

    private static final Logger log = LoggerFactory.getLogger(StatementController.class);

    private final StatementRepository statementsRepo;
    private final PostClient postClient;

    StatementController(StatementRepository statementRepository, PostClient postClient) {
        this.statementsRepo = statementRepository;
        this.postClient = postClient;
    }

    @GetMapping("/statement/{statementId}")
    HttpEntity<EntityModel<Statement>> react(@PathVariable Statement.StatementId statementId) {
        Statement statement = statementsRepo
                .findById(statementId)
                .orElseThrow(() -> new ResourceNotFoundException("Statement not found"));
        return ResponseEntity.ok(EntityModel.of(statement));
    }

    @PostMapping("/{statementId}/react")
    HttpEntity<?> react(@PathVariable Statement.StatementId statementId, @RequestParam String reaction) {

        log.info("Fooo {}", reaction);

        //        statementsRepo.findById(statementId).orElseThrow(() -> new
        // ResponseStatusException(HttpStatusCode.valueOf(404)));
        Post post = postClient.findById(Integer.valueOf(reaction));

        return ResponseEntity.ok(post);
    }
}
