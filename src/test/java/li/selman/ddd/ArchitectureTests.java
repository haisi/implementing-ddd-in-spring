package li.selman.ddd;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.CompositeArchRule;
import de.rweisleder.archunit.spring.framework.SpringAsyncRules;
import de.rweisleder.archunit.spring.framework.SpringCacheRules;
import org.jmolecules.archunit.JMoleculesArchitectureRules;
import org.jmolecules.archunit.JMoleculesDddRules;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNullApi;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

@AnalyzeClasses(packagesOf = ImplementingDddApplication.class, importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTests {

    private static final String ROOT_PACKAGE = "li.selman.ddd";

    @ArchTest
    ArchRule dddRules = JMoleculesDddRules.all();

    @ArchTest
    ArchRule onionRules = JMoleculesArchitectureRules.ensureOnionSimple();

    /**
     * Spring Cacheable rules
     */
    ArchRule springCacheableRules = CompositeArchRule //
            .of(SpringCacheRules.CacheableMethodsNotCalledFromSameClass)
            .of(SpringCacheRules.CacheableMethodsAreProxyable)
            .allowEmptyShould(true);

    /**
     * Spring Async rules
     */
    ArchRule springAsyncRules = CompositeArchRule //
            .of(SpringAsyncRules.AsyncMethodsNotCalledFromSameClass)
            .of(SpringAsyncRules.AsyncMethodsAreProxyable)
            .of(SpringAsyncRules.AsyncMethodsHaveSuitableReturnType)
            .allowEmptyShould(true);

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

    /** Enforce that all packages contain a `package-info.java` annotated with `@NonNullApi` */
    @ArchTest
    void packagesShouldBeAnnotated(JavaClasses classes) {
        var rootPackage = classes.getPackage(ROOT_PACKAGE);
        var violations = rootPackage.getSubpackagesInTree().stream()
                .filter(pkg -> !pkg.isAnnotatedWith(NonNullApi.class))
                .map(pkg -> pkg.getDescription() + " is not annotated with @" + NonNullApi.class.getSimpleName());
        assertThat(violations).as("violations").isEmpty();
    }
}
