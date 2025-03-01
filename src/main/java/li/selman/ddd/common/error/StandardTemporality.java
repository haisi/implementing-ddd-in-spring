package li.selman.ddd.common.error;

public enum StandardTemporality implements Temporality {
    PERMANENT,
    TEMPORARY_APPLICATION,
    TEMPORARY_GLOBAL,
    ;

    @Override
    public String get() {
        return name();
    }
}
