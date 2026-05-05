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
    void loggingPackageShouldNotDependOnOtherPackages() {
        final ArchRule rule = noClasses().that()
                .resideInAPackage(BASE_PACKAGE + ".logging..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        BASE_PACKAGE + ".security..",
                        BASE_PACKAGE + ".tools.."
                );
        rule.check(classes);
    }

    @Test
    void securityPackageShouldNotDependOnOtherPackages() {
        final ArchRule rule = noClasses().that()
                .resideInAPackage(BASE_PACKAGE + ".security..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        BASE_PACKAGE + ".logging.."
                );
        rule.check(classes);
    }
}
