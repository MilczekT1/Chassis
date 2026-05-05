package io.github.milczekt1.chassis.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ObservabilityModularityTests {

    private static final String BASE_PACKAGE = "io.github.milczekt1.chassis";
    private static final JavaClasses classes = new ClassFileImporter()
            .importPackages(BASE_PACKAGE + ".observability..");

    @Test
    void observabilityPackageShouldNotDependOnOtherChassisPackages() {
        final ArchRule rule = noClasses().that()
                .resideInAPackage(BASE_PACKAGE + ".observability..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        BASE_PACKAGE + ".security..",
                        BASE_PACKAGE + ".tools..",
                        BASE_PACKAGE + ".logging..",
                        BASE_PACKAGE + ".errorhandling.."
                );
        rule.check(classes);
    }
}
