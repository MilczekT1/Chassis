package io.github.milczekt1.chassis.architecture.production;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class PackageModularityTests {
    public static final JavaClasses classes;
    public static final String BASE_PACKAGE = "io.github.milczekt1.chassis";

    static {
        classes = new ClassFileImporter()
                .importPackages(BASE_PACKAGE);
    }

    @Test
    void exceptionPackageShouldNotDependOnOtherPackages() {
        // Given:
        ArchRule rule = noClasses().that()
                .resideInAPackage(BASE_PACKAGE + ".errorhandling..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        BASE_PACKAGE + ".configuration..",
                        BASE_PACKAGE + ".tools..",
                        BASE_PACKAGE + ".logging.."
                );
        // Then:
        rule.check(classes);
    }

    @Test
    void loggingPackageShouldNotDependOnOtherPackages() {
        // Given:
        ArchRule rule = noClasses().that()
                .resideInAPackage(BASE_PACKAGE + ".logging..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        BASE_PACKAGE + ".configuration..",
                        BASE_PACKAGE + ".tools..",
                        BASE_PACKAGE + ".errorhandling.."
                );
        // Then:
        rule.check(classes);
    }
}
