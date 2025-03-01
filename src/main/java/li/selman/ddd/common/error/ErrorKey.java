package li.selman.ddd.common.error;

public interface ErrorKey {

    Integer getPublicErrorCode();
    /**
     * An ErrorKey represents two entries in resource
     * {@code /messages/errors/application-errors-messages.properties}
     * and its translations.
     * <p>
     * For a key (for example 'bad-request'), you must define an error title and detail.
     *
     * <pre>
     * {@code
     * error.bad-request.title=Bad request
     * error.bad-request.detail=You send a bad request: {{ code }}
     * }
     * </pre>
     *
     * @return key
     */
    String getTranslationKey();
}
