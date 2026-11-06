package io.github.milczekt1.chassis.rules.internal;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;
import io.github.milczekt1.corral.groups.ConcurrencyRulesGroup;
import io.github.milczekt1.corral.groups.JavaCorrectnessRulesGroup;
import io.github.milczekt1.corral.groups.LoggingRulesGroup;
import io.github.milczekt1.corral.groups.SpringRulesGroup;
import io.github.milczekt1.corral.groups.TestingRulesGroup;
import io.github.milczekt1.corral.rules.concurrency.nofuturegetwithouttimeout.NoFutureGetWithoutTimeoutRule;
import io.github.milczekt1.corral.rules.java.nointernalapi.NoInternalApiRule;
import io.github.milczekt1.corral.rules.java.nolegacydateapi.NoLegacyDateApiRule;
import io.github.milczekt1.corral.rules.java.nolegacysynchronizedcollections.NoLegacySynchronizedCollectionsRule;
import io.github.milczekt1.corral.rules.java.nooptionalfields.NoOptionalFieldsRule;
import io.github.milczekt1.corral.rules.java.nooptionalmethodparameters.NoOptionalMethodParametersRule;
import io.github.milczekt1.corral.rules.java.serializablemustdeclareserialversionuid.SerializableMustDeclareSerialVersionUidRule;
import io.github.milczekt1.corral.rules.naming.lowercasepackages.LowercasePackagesRule;
import io.github.milczekt1.corral.rules.spring.configurationpropertiesmustuseprojectprefix.ConfigurationPropertiesMustUseProjectPrefixRule;
import io.github.milczekt1.corral.rules.spring.novalueonfield.NoValueOnFieldRule;
import io.github.milczekt1.corral.rules.testing.testclassnamingconvention.TestClassNamingConventionRule;

/**
 * The Corral rules every Chassis module runs. Wire it from exactly one test class per module: a rule id is a
 * freeze-store key.
 */
public final class ChassisArchitectureRules {

    @ArchTest
    static final ArchTests logging = ArchTests.in(LoggingRulesGroup.class);

    @ArchTest
    static final ArchTests javaCorrectness = ArchTests.in(JavaCorrectnessRulesGroup.class);

    @ArchTest
    static final ArchTests concurrency = ArchTests.in(ConcurrencyRulesGroup.class);

    @ArchTest
    static final ArchTests spring = ArchTests.in(SpringRulesGroup.class);

    @ArchTest
    static final ArchTests testing = ArchTests.in(TestingRulesGroup.class);

    @ArchTest
    static final ArchTests configurationPropertiesPrefix = ArchTests.in(ConfigurationPropertiesMustUseProjectPrefixRule.class);

    @ArchTest
    static final ArchTests testClassNaming = ArchTests.in(TestClassNamingConventionRule.class);

    @ArchTest
    static final ArchTests serialVersionUid = ArchTests.in(SerializableMustDeclareSerialVersionUidRule.class);

    @ArchTest
    static final ArchTests noValueOnField = ArchTests.in(NoValueOnFieldRule.class);

    @ArchTest
    static final ArchTests noLegacyDateApi = ArchTests.in(NoLegacyDateApiRule.class);

    @ArchTest
    static final ArchTests noOptionalFields = ArchTests.in(NoOptionalFieldsRule.class);

    @ArchTest
    static final ArchTests noOptionalMethodParameters = ArchTests.in(NoOptionalMethodParametersRule.class);

    @ArchTest
    static final ArchTests noFutureGetWithoutTimeout = ArchTests.in(NoFutureGetWithoutTimeoutRule.class);

    @ArchTest
    static final ArchTests noInternalApi = ArchTests.in(NoInternalApiRule.class);

    @ArchTest
    static final ArchTests noLegacySynchronizedCollections = ArchTests.in(NoLegacySynchronizedCollectionsRule.class);

    @ArchTest
    static final ArchTests lowercasePackages = ArchTests.in(LowercasePackagesRule.class);

    private ChassisArchitectureRules() {
    }
}
