package li.selman.ddd.statement;

public interface CustomizedSave<T> {
    <S extends T> S save(S entity);
}
