package li.selman.ddd.statement.infrastructure.persistence;

import li.selman.ddd.common.MyDataJpaTest;
import li.selman.ddd.statement.Statement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@MyDataJpaTest(JpaStatementRepository.class)
class JpaStatementRepositoryIT {

    @Autowired
    JpaStatementRepository jpaStatementRepository;

    @Test
    void findAll() {
        jpaStatementRepository.findByState(Statement.State.CLOSED);
    }

}
