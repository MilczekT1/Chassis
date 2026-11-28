package io.github.milczekt1.chassis.rewrite;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import java.nio.file.Path;

import static io.github.milczekt1.chassis.rewrite.ChassisTestRepository.LEGACY;
import static io.github.milczekt1.chassis.rewrite.ChassisTestRepository.target;
import static io.github.milczekt1.chassis.rewrite.TestPoms.CONTRACT_BASE;
import static io.github.milczekt1.chassis.rewrite.TestPoms.CONTRACT_TYPES;
import static io.github.milczekt1.chassis.rewrite.TestPoms.STUB_RUNNER_TEST;
import static io.github.milczekt1.chassis.rewrite.TestPoms.dependency;
import static io.github.milczekt1.chassis.rewrite.TestPoms.modulePom;
import static io.github.milczekt1.chassis.rewrite.TestPoms.withContractBom;
import static org.openrewrite.java.Assertions.java;
import static org.openrewrite.java.Assertions.mavenProject;
import static org.openrewrite.java.Assertions.srcMainJava;
import static org.openrewrite.java.Assertions.srcTestJava;
import static org.openrewrite.maven.Assertions.pomXml;

/**
 * Runs on {@link ChassisTestRepository#LEGACY} poms: {@code UpgradeChassis} applies the recipe before the version bump.
 */
// rewriteRun is the assertion: it fails the test when the recipe's result differs from the expected sources
@SuppressWarnings("java:S2699")
class AddSpringCloudContractWhereUsedTest implements RewriteTest {

    private static final String SPRING_CLOUD = "org.springframework.cloud";
    private static final String VERIFIER = "spring-cloud-starter-contract-verifier";

    @TempDir
    Path repository;

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipeFromResources("io.github.milczekt1.chassis.AddSpringCloudContractWhereUsed")
                .executionContext(ChassisTestRepository.executionContext(repository, LEGACY, target()))
                .parser(JavaParser.fromJavaVersion().dependsOn(CONTRACT_TYPES));
    }

    @Test
    void addsVerifierWhereTestsUseIt() {
        rewriteRun(mavenProject("app",
                pomXml(modulePom("app", LEGACY, ""),
                        withContractBom(modulePom("app", LEGACY, dependency(SPRING_CLOUD, VERIFIER, "test")))),
                srcTestJava(java(CONTRACT_BASE))));
    }

    @Test
    void addsVerifierWhereTestsUseTheContractSpec() {
        rewriteRun(mavenProject("app",
                pomXml(modulePom("app", LEGACY, ""),
                        withContractBom(modulePom("app", LEGACY, dependency(SPRING_CLOUD, VERIFIER, "test")))),
                srcTestJava(java("""
                        package com.example;

                        import org.springframework.cloud.contract.spec.internal.RegexPatterns;

                        class TimestampIT {
                            RegexPatterns patterns;
                        }
                        """))));
    }

    @Test
    void addsStubRunnerWhereTestsUseIt() {
        rewriteRun(mavenProject("app",
                pomXml(modulePom("app", LEGACY, ""),
                        withContractBom(modulePom("app", LEGACY,
                                dependency(SPRING_CLOUD, "spring-cloud-contract-stub-runner", "test")))),
                srcTestJava(java(STUB_RUNNER_TEST))));
    }

    @Test
    void addsVerifierWhereThePluginRuns() {
        rewriteRun(mavenProject("app",
                pomXml(pluginModulePom(""),
                        withContractBom(pluginModulePom(dependency(SPRING_CLOUD, VERIFIER, "test"))))));
    }

    @Test
    void importsBomWhereModuleDeclaresContractDependency() {
        String verifier = dependency(SPRING_CLOUD, VERIFIER, "test");
        rewriteRun(mavenProject("app",
                pomXml(modulePom("app", LEGACY, verifier), withContractBom(modulePom("app", LEGACY, verifier)))));
    }

    @Test
    void leavesModuleWithoutContractUsage() {
        rewriteRun(mavenProject("app",
                pomXml(modulePom("app", LEGACY, "")),
                srcMainJava(java("package com.example;\n\nclass Plain {\n}\n"))));
    }

    private static String pluginModulePom(String testDependencies) {
        return """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <parent>
                        <groupId>io.github.milczekt1</groupId>
                        <artifactId>chassis-parent</artifactId>
                        <version>%s</version>
                        <relativePath/>
                    </parent>
                    <groupId>com.example</groupId>
                    <artifactId>app</artifactId>
                    <version>1.0.0</version>
                    <dependencies>
                        <dependency>
                            <groupId>io.github.milczekt1</groupId>
                            <artifactId>chassis-tools-test</artifactId>
                            <scope>test</scope>
                        </dependency>
                %s    </dependencies>
                    <build>
                        <plugins>
                            <plugin>
                                <groupId>org.springframework.cloud</groupId>
                                <artifactId>spring-cloud-contract-maven-plugin</artifactId>
                                <extensions>true</extensions>
                            </plugin>
                        </plugins>
                    </build>
                </project>
                """.formatted(LEGACY, testDependencies);
    }
}
