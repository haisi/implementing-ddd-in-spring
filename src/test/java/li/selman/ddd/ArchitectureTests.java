package li.selman.ddd;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.CompositeArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.GeneralCodingRules;
import de.rweisleder.archunit.spring.framework.SpringAsyncRules;
import de.rweisleder.archunit.spring.framework.SpringCacheRules;
import de.rweisleder.archunit.spring.framework.SpringComponentPredicates;
import li.selman.ddd.common.error.MyException;
import org.jmolecules.archunit.JMoleculesArchitectureRules;
import org.jmolecules.archunit.JMoleculesDddRules;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.lang.NonNullApi;
import org.springframework.lang.NonNullFields;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

import java.util.stream.Stream;

import static de.rweisleder.archunit.spring.SpringAnnotationPredicates.springAnnotatedWith;
import static org.assertj.core.api.Assertions.assertThat;

@AnalyzeClasses(packagesOf = ImplementingDddApplication.class, importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTests {

    private static final String ROOT_PACKAGE = "li.selman.ddd";

    @ArchTest
    ArchRule dddRules = JMoleculesDddRules.all();

    @ArchTest
    ArchRule onionRules = JMoleculesArchitectureRules.ensureOnionSimple();

    @ArchTest
    ArchRule generalCodingRules = CompositeArchRule //
            .of(GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING)
            .and(GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION)
            .and(GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS)
            .and(GeneralCodingRules.DEPRECATED_API_SHOULD_NOT_BE_USED)
            .and(GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS)
            .allowEmptyShould(true);

    /**
     * Spring Cacheable rules
     */
    @ArchTest
    ArchRule springCacheableRules = CompositeArchRule.of(SpringCacheRules.CacheableMethodsAreProxyable)
            .and(SpringCacheRules.CacheableMethodsNotCalledFromSameClass)
            .allowEmptyShould(true);

    /**
     * Spring Async rules
     */
    @ArchTest
    ArchRule springAsyncRules = CompositeArchRule //
            .of(SpringAsyncRules.AsyncMethodsNotCalledFromSameClass)
            .and(SpringAsyncRules.AsyncMethodsAreProxyable)
            .and(SpringAsyncRules.AsyncMethodsHaveSuitableReturnType)
            .allowEmptyShould(true);

    @ArchTest
    ArchRule springConfigurationNaming = ArchRuleDefinition.classes()
            .that(SpringComponentPredicates.springConfiguration())
            .and(DescribedPredicate.not(springAnnotatedWith(SpringBootApplication.class)))
            .should()
            .haveSimpleNameEndingWith("Configuration")
            .as("Custom spring configuration class names must end with Configuration");

    /**
     * Verifying application module structure
     *
     * @see <a href="https://docs.spring.io/spring-modulith/reference/verification.html">Reference
     *     documentation on verification</a>
     */
    ApplicationModules modules = ApplicationModules.of(ImplementingDddApplication.class);

    @Test
    void writeDocumentationSnippets() {
        modules.forEach(System.out::println);

        new Documenter(modules)
                .writeModuleCanvases()
                .writeModulesAsPlantUml(
                        Documenter.DiagramOptions.defaults().withStyle(Documenter.DiagramOptions.DiagramStyle.C4))
                .writeIndividualModulesAsPlantUml();

        modules.verify();
    }

    @ArchTest
    ArchRule allExceptionsShouldInheritFromMyException = ArchRuleDefinition.classes()
            .that()
            .areAssignableTo(Throwable.class)
            .should()
            .beAssignableTo(MyException.class);

    /** Enforce that all packages contain a `package-info.java` annotated with `@NonNullApi` and `@NonNullFields.
     * Run {@link PackageInfoGenerator} to fix */
    @ArchTest
    void packagesShouldBeAnnotated(JavaClasses classes) {
        var rootPackage = classes.getPackage(ROOT_PACKAGE);
        Stream<String> violations = rootPackage.getSubpackagesInTree().stream()
                .filter(pkg -> !pkg.isAnnotatedWith(NonNullApi.class) || !pkg.isAnnotatedWith(NonNullFields.class))
                .map(pkg -> pkg.getDescription() + " is not annotated with @" + NonNullApi.class.getSimpleName()
                        + " and @" + NonNullFields.class.getSimpleName());

        assertThat(violations)
                .as("Not all packages contain a package-info.java file with the required nullability annotations. "
                        + "Run PackageInfoGenerator to fix.")
                .isEmpty();
    }
}
