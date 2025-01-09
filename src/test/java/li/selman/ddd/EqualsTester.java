package li.selman.ddd;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public final class EqualsTester {

    public static <T> void testJpaEquals(Class<T> type) {
        EqualsVerifier.forClass(type)
                .usingGetClass() // required as instanceOf is wrong for JPA
                .suppress(
                        Warning.STRICT_HASHCODE, // see https://stackoverflow.com/a/75428675
                        Warning.SURROGATE_KEY //
                        )
                .verify();
    }

    public static <T> void testJpaEqualsWithNaturalId(Class<T> type) {
        EqualsVerifier.forClass(type)
                .usingGetClass() // required as instanceOf is wrong for JPA
                .suppress(
                        Warning.STRICT_HASHCODE // see https://stackoverflow.com/a/75428675
                        )
                .verify();
    }
}
