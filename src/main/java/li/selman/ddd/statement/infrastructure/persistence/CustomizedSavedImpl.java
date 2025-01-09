package li.selman.ddd.statement.infrastructure.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import li.selman.ddd.statement.CustomizedSave;
import li.selman.ddd.statement.Statement;

@SuppressWarnings("NullAway.Init")
public class CustomizedSavedImpl implements CustomizedSave<Statement> {

    @SuppressWarnings("unused")
    private @PersistenceContext EntityManager entityManager;

    @Override
    public <S extends Statement> S save(S entity) {
        return entity;
    }
}
