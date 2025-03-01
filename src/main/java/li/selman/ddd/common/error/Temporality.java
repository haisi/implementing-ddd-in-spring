package li.selman.ddd.common.error;

/**
 * Most errors are permanent.
 * Meaning, no matter how often we retry the same, failing operation, the outcome will remain an error.
 * However, certain types of errors can be resolved after a couple of retries.
 * For example optimistic locking exceptions or 404 in case of an eventually consistent system environment.
 * <p>
 * Whether the retry mechanism is resolved on application layer or delegated to an external service is unimpor
 */
public interface Temporality {
    String get();
}
