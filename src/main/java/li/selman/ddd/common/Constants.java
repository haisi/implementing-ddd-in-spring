package li.selman.ddd.common;

import java.util.Locale;
import java.util.Set;

public class Constants {

    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    public static final Set<Locale> SUPPORTED_LOCALES = Set.of(DEFAULT_LOCALE, Locale.GERMAN);
}
