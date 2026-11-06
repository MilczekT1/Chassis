package io.github.milczekt1.chassis.rules.internal;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;
import io.github.milczekt1.corral.groups.LoggingRulesGroup;

/**
 * The Corral rules every Chassis module runs. Wire it from exactly one test class per module: a rule id is a
 * freeze-store key.
 */
public final class ChassisArchitectureRules {

    @ArchTest
    static final ArchTests logging = ArchTests.in(LoggingRulesGroup.class);

    private ChassisArchitectureRules() {
    }
}
