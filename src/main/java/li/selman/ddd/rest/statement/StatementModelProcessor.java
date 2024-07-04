package li.selman.ddd.rest.statement;

import li.selman.ddd.statement.Statement;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class StatementModelProcessor implements RepresentationModelProcessor<EntityModel<Statement>> {
  @Override
  public EntityModel<Statement> process(EntityModel<Statement> model) {
    System.out.println("whyyy are you not called?");
    return model;
  }
}
