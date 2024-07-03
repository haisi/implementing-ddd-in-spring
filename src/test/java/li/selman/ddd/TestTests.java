package li.selman.ddd;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.SourceCodeLocation;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import java.net.URI;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import li.selman.ddd.common.DeepCopyAggregateForInMemoryRepository;
import org.jmolecules.ddd.types.AggregateRoot;
import org.jmolecules.ddd.types.Repository;

@AnalyzeClasses(
    packagesOf = ImplementingDddApplication.class,
    importOptions = ImportOption.DoNotIncludeJars.class)
class TestTests {

  @ArchTest
  private final ArchRule aggregatesHaveTestFixtures =
      ArchRuleDefinition.classes()
          .that()
          .areAssignableTo(AggregateRoot.class)
          .should(haveTestFixture());

  /**
   * Read chapter "Collection-Oriented Repositories" in "Implementing Domain-Driven-Design" by
   * Vaughn Vernon for further details.
   */
  @ArchTest
  private final ArchRule repositoriesHaveInMemoryImplementation =
      ArchRuleDefinition.classes()
          .that()
          .areAssignableTo(Repository.class)
          .should(haveInMemoryImplementation());

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
        events.add(
            SimpleConditionEvent.violated(
                item,
                String.format(
                    "Could not find test fixture '%s' for aggregate '%s'",
                    testFixtureClassName, item.getName())));
      }
    }
  }

  private RepositoryHasInMemoryImplementation haveInMemoryImplementation() {
    return new RepositoryHasInMemoryImplementation();
  }

  private static class RepositoryHasInMemoryImplementation extends ArchCondition<JavaClass> {

    public RepositoryHasInMemoryImplementation() {
      super("have a in-memory implementation");
    }

    @Override
    public void check(JavaClass repository, ConditionEvents events) {
      if (TEST_LOCATION.test(repository)) {
        // Skip test classes
        return;
      }

      String inMemoryRepoClassName =
          repository.getPackageName() + ".InMemory" + repository.getSimpleName();
      try {
        Class<?> inMemoryRepoClass = Class.forName(inMemoryRepoClassName);
        if (classDoesNotImplementItem(inMemoryRepoClass, repository)) {
          events.add(
              SimpleConditionEvent.violated(
                  repository,
                  String.format(
                      "'%s' does not implement its repository interface '%s'",
                      inMemoryRepoClassName, repository.getName())));
        } else if (classDoesNotImplementInterface(
            inMemoryRepoClass, DeepCopyAggregateForInMemoryRepository.class)) {
          events.add(
              SimpleConditionEvent.violated(
                  repository,
                  String.format(
                      "'%s' does not implement interface '%s'",
                      inMemoryRepoClassName,
                      DeepCopyAggregateForInMemoryRepository.class.getSimpleName())));
        }
      } catch (ClassNotFoundException e) {
        events.add(
            SimpleConditionEvent.violated(
                repository,
                String.format(
                    "Could not find in-memory implementation '%s' for repository '%s'",
                    inMemoryRepoClassName, repository.getName())));
      }
    }
  }

  private static boolean classDoesNotImplementInterface(
      Class<?> implementation, Class<?> interfazeToImpl) {
    for (Class<?> interfaze : implementation.getInterfaces()) {
      if (interfazeToImpl.equals(interfaze)) {
        return false;
      }
    }
    return true;
  }

  private static boolean classDoesNotImplementItem(Class<?> implementation, JavaClass item) {
    for (Class<?> interfaze : implementation.getInterfaces()) {
      if (item.getFullName().equals(interfaze.getName())) {
        return false;
      }
    }
    return true;
  }

  static final PatternPredicate MAVEN_TEST_PATTERN =
      new PatternPredicate(".*/target/test-classes/.*");
  static final PatternPredicate GRADLE_TEST_PATTERN =
      new PatternPredicate(".*/build/classes/([^/]+/)?test/.*");
  static final PatternPredicate INTELLIJ_TEST_PATTERN = new PatternPredicate(".*/out/test/.*");
  static final Predicate<JavaClass> TEST_LOCATION =
      MAVEN_TEST_PATTERN.or(GRADLE_TEST_PATTERN).or(INTELLIJ_TEST_PATTERN);
  static final Predicate<JavaClass> NO_TEST_LOCATION = TEST_LOCATION.negate();

  private static class PatternPredicate implements Predicate<JavaClass> {
    private final Pattern pattern;

    PatternPredicate(String pattern) {
      this.pattern = Pattern.compile(pattern);
    }

    @Override
    public boolean test(JavaClass input) {
      SourceCodeLocation sourceCodeLocation = input.getSourceCodeLocation();
      URI uri = sourceCodeLocation.getSourceClass().getSource().get().getUri();
      return pattern.matcher(uri.toString()).matches();
    }
  }
}
