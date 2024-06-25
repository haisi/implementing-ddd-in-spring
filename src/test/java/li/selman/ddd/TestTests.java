package li.selman.ddd;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.properties.CanBeAnnotated;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jmolecules.ddd.types.AggregateRoot;
import org.springframework.lang.NonNullApi;

import static org.assertj.core.api.Assertions.assertThat;

@AnalyzeClasses(packagesOf = ImplementingDddApplication.class, importOptions = ImportOption.DoNotIncludeJars.class)
class TestTests {

    @ArchTest
    private final ArchRule annotationInheritance = ArchRuleDefinition
            .classes().that()
            .areAssignableTo(AggregateRoot.class)
            .should(haveTestFixture());

    private AggregateHasTestFixture haveTestFixture() {
        return new AggregateHasTestFixture();
    }

    private static class AggregateHasTestFixture extends ArchCondition<JavaClass> {

        public AggregateHasTestFixture() {
            super("have a test fixture");
        }

        @Override
        public void check(JavaClass item, ConditionEvents events) {
            String testFixtureClassName = item.getFullName() + "Fixture";
            try {
                Class.forName(testFixtureClassName);
            } catch (ClassNotFoundException e) {
                events.add(SimpleConditionEvent.violated(item,
                        String.format("Could not find test fixture '%s' for aggregate '%s'", testFixtureClassName, item.getName())));
            }
        }
    }

    @ArchTest
    void packagesShouldBeAnnotated(JavaClasses classes) {
//        var violations = rootPackage.getSubpackagesInTree().stream()
//                .filter(pkg -> !pkg.isAnnotatedWith(NonNullApi.class))
//                .map(pkg -> pkg.getDescription() + " is not annotated with @" + NonNullApi.class.getSimpleName());
//        assertThat(violations).as("violations").isEmpty();
    }


}
