package li.selman.ddd.common.error;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static li.selman.ddd.common.Constants.DEFAULT_LOCALE;
import static li.selman.ddd.common.Constants.SUPPORTED_LOCALES;
import static li.selman.ddd.common.error.ApplicationErrorHandler.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class StandardErrorKeyTest {

    static ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();

    @BeforeAll
    static void init() {
        source.setBasename("classpath:/i18n/errors/application-errors-messages");
        source.setDefaultEncoding("UTF-8");
    }

    @Test
    void eachErrorKeyIsUnique() {
        Set<Integer> uniqueErrorsKeys = Arrays.stream(StandardErrorKey.values())
                .map(StandardErrorKey::getPublicErrorCode)
                .collect(Collectors.toSet());
        assertThat(StandardErrorKey.values()).hasSize(uniqueErrorsKeys.size());
    }

    @ParameterizedTest
    @MethodSource("errorKeyLocaleCombination")
    void allErrorKeysHaveTranslationsForTitleAndDetail(StandardErrorKey key, Locale locale) {
        assertSoftly(softAssertions -> {
            softAssertions
                    .assertThat(source.getMessage(getMessageCode(key, TITLE_SUFFIX), null, locale))
                    .isNotBlank();
            softAssertions
                    .assertThat(source.getMessage(getMessageCode(key, DETAIL_SUFFIX), null, locale))
                    .isNotBlank();
        });
    }

    @Test
    void ensureTranslationFilesExistForAllSupportedLocales() throws URISyntaxException {
        String i18nPath = "i18n/errors";
        String fileNamePattern = "application-errors-messages%s.properties";

        ClassLoader classLoader = getClass().getClassLoader();
        URL resourceUrl = classLoader.getResource(i18nPath);
        assertThat(resourceUrl).isNotNull();

        Path resourcePath = Paths.get(resourceUrl.toURI());

        // For each supported locale
        for (Locale locale : SUPPORTED_LOCALES) {
            String suffix = DEFAULT_LOCALE.equals(locale) ? "" : "_" + locale.getLanguage();
            String expectedFileName = String.format(fileNamePattern, suffix);
            Path expectedFile = resourcePath.resolve(expectedFileName);

            assertThat(Files.exists(expectedFile))
                    .withFailMessage(
                            "Missing translation file for locale %s: %s", locale.getLanguage(), expectedFileName)
                    .isTrue();
        }
    }

    @Test
    void ensureNoExtraTranslationFiles() throws IOException, URISyntaxException {
        String i18nPath = "i18n/errors";
        Pattern filePattern = Pattern.compile("application-errors-messages(|_\\w+)\\.properties");

        ClassLoader classLoader = getClass().getClassLoader();
        URL resourceUrl = classLoader.getResource(i18nPath);
        assertThat(resourceUrl).isNotNull();

        Path resourcePath = Paths.get(resourceUrl.toURI());

        try (Stream<Path> files = Files.list(resourcePath)) {
            files.forEach(file -> {
                Matcher matcher = filePattern.matcher(file.getFileName().toString());
                if (matcher.matches()) {
                    // Capture the group in a final variable
                    final String suffix = matcher.group(1);

                    if (suffix.isEmpty()) {
                        // Default file (English)
                        assertThat(SUPPORTED_LOCALES).contains(Locale.ENGLISH);
                    } else {
                        // Remove leading underscore and store in final variable
                        final String locale = suffix.substring(1);
                        assertThat(SUPPORTED_LOCALES)
                                .withFailMessage("Found translation file for unsupported locale: %s", locale)
                                .anyMatch(supported -> supported.getLanguage().equals(locale));
                    }
                }
            });
        }
    }

    static Stream<Arguments> errorKeyLocaleCombination() {
        List<Arguments> combinations = new ArrayList<>();

        for (var first : StandardErrorKey.values()) {
            for (Locale second : SUPPORTED_LOCALES) {
                combinations.add(Arguments.of(first, second));
            }
        }
        return combinations.stream();
    }
}
