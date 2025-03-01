package li.selman.ddd.common.error;

public enum StandardErrorKey implements ErrorKey {
    BAD_REQUEST(1, "bad-request"),
    ILLEGAL_ARGUMENT(2, "internal-server-error"),
    INTERNAL_SERVER_ERROR(3, "internal-server-error"),
    NULL_POINTER(4, "internal-server-error"),
    PROCESS_CONFLICT(4, "stale-state"),
    ;

    /**
     * We do not want to expose error codes from which user my infer too much information.
     */
    private final Integer publicErrorCode;

    /**
     * The translations key which gets resolved to i18n text.
     * For multiple error codes we can re-use the same translation key.
     */
    private final String translationKey;

    StandardErrorKey(Integer publicErrorCode, String translationKey) {
        this.publicErrorCode = publicErrorCode;
        this.translationKey = translationKey;
    }

    @Override
    public String getTranslationKey() {
        return translationKey;
    }

    @Override
    public Integer getPublicErrorCode() {
        return publicErrorCode;
    }
}
