package io.github.milczekt1.chassis.rules.internal;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;

/**
 * The architecture rules every Chassis module runs. Wire it from exactly one test class per module: a rule id is
 * a freeze-store key.
 */
public final class ChassisInternalArchitectureRules {

    @ArchTest
    static final ArchTests corral = ArchTests.in(CorralRules.class);

    private ChassisInternalArchitectureRules() {
    }
}
