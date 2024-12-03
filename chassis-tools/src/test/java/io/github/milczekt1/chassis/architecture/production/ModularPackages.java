package io.github.milczekt1.chassis.architecture.production;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class ModularPackages {
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
                .resideInAPackage(BASE_PACKAGE + ".exceptions" + "..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        BASE_PACKAGE + ".configuration..",
                        BASE_PACKAGE + ".tools..");
        // Then:
        rule.check(classes);
    }
}
