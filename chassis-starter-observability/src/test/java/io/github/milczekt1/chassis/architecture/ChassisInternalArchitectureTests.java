package io.github.milczekt1.chassis.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;
import io.github.milczekt1.chassis.rules.internal.ChassisArchitectureRules;
import io.github.milczekt1.chassis.rules.internal.CurrentModuleLocations;

@AnalyzeClasses(locations = CurrentModuleLocations.class)
class ChassisInternalArchitectureTests {

    @ArchTest
    static final ArchTests chassis = ArchTests.in(ChassisArchitectureRules.class);
}
