package li.selman.ddd.statement.infrastructure.persistence;

import li.selman.ddd.common.container.EnablePostgresContainer;
import li.selman.ddd.statement.Statement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@DataJpaTest(
        includeFilters =
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JpaStatementRepository.class))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnablePostgresContainer
class JpaStatementRepositoryIT {

    // https://stackoverflow.com/a/68890310 prefer the initalizer approach

    @Autowired
    JpaStatementRepository jpaStatementRepository;

    @Test
    void findAll() {
        jpaStatementRepository.findByState(Statement.State.CLOSED);
    }

}
