package io.github.milczekt1.chassis.rewrite;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import java.nio.file.Path;

import static io.github.milczekt1.chassis.rewrite.ChassisTestRepository.LEGACY;
import static io.github.milczekt1.chassis.rewrite.ChassisTestRepository.target;
import static io.github.milczekt1.chassis.rewrite.TestPoms.MONGO_TEST;
import static io.github.milczekt1.chassis.rewrite.TestPoms.REST_CLIENT;
import static io.github.milczekt1.chassis.rewrite.TestPoms.dependency;
import static io.github.milczekt1.chassis.rewrite.TestPoms.modulePom;
import static org.openrewrite.java.Assertions.java;
import static org.openrewrite.java.Assertions.mavenProject;
import static org.openrewrite.java.Assertions.srcMainJava;
import static org.openrewrite.java.Assertions.srcTestJava;
import static org.openrewrite.maven.Assertions.pomXml;

class UpgradeChassisTest implements RewriteTest {

    @TempDir
    Path repository;

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipeFromResources("io.github.milczekt1.chassis.UpgradeChassis")
                .executionContext(ChassisTestRepository.executionContext(repository, LEGACY, target()))
                .parser(JavaParser.fromJavaVersion()
                        .classpath("testcontainers", "testcontainers-mongodb", "spring-boot-restclient", "jspecify", "spring-context")
                        .dependsOn(
                                "package io.github.milczekt1.chassis.test.mongo; public class ChassisTestMongoCleanupConfiguration {}",
                                "package com.example; public class OtherConfig {}"));
    }

    @Test
    void upgradesLlamaHouseShapedProject() {
        rewriteRun(
                pomXml("""
                        <project>
                            <modelVersion>4.0.0</modelVersion>
                            <groupId>com.example</groupId>
                            <artifactId>llamahouse</artifactId>
                            <version>1.0.0</version>
                            <packaging>pom</packaging>
                            <modules>
                                <module>app</module>
                                <module>inventory</module>
                                <module>rules</module>
                            </modules>
                        </project>
                        """),
                mavenProject("app",
                        pomXml(modulePom("app", LEGACY, ""),
                                modulePom("app", target(), dependency("org.testcontainers", "testcontainers-mongodb", "test"))),
                        srcTestJava(java(MONGO_TEST))),
                mavenProject("inventory",
                        pomXml(inventoryPom(LEGACY, ""),
                                inventoryPom(target(), dependency("org.springframework.boot", "spring-boot-restclient", null))),
                        srcMainJava(java(REST_CLIENT)),
                        srcTestJava(java(
                                """
                                package com.example;

                                import io.github.milczekt1.chassis.test.mongo.ChassisTestMongoCleanupConfiguration;
                                import org.springframework.context.annotation.Import;

                                @Import({OtherConfig.class, ChassisTestMongoCleanupConfiguration.class})
                                class InventoryIT {
                                }
                                """,
                                """
                                package com.example;

                                import org.springframework.context.annotation.Import;

                                @Import({OtherConfig.class})
                                class InventoryIT {
                                }
                                """))),
                mavenProject("rules",
                        pomXml(rulesPom(LEGACY), rulesPom(target()))));
    }

    @Test
    void changesNothingWhenTargetIsNotPublished() {
        rewriteRun(
                spec -> spec.executionContext(ChassisTestRepository.executionContext(repository.resolve("unpublished"), LEGACY)),
                mavenProject("app",
                        pomXml(modulePom("app", LEGACY, "")),
                        srcTestJava(java(MONGO_TEST))));
    }

    private static String inventoryPom(String chassisVersion, String extraDependencies) {
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
                    <artifactId>inventory</artifactId>
                    <version>1.0.0</version>
                    <dependencyManagement>
                        <dependencies>
                            <dependency>
                                <groupId>io.github.milczekt1</groupId>
                                <artifactId>chassis-bom</artifactId>
                                <version>${project.parent.version}</version>
                                <type>pom</type>
                                <scope>import</scope>
                            </dependency>
                        </dependencies>
                    </dependencyManagement>
                    <dependencies>
                        <dependency>
                            <groupId>io.github.milczekt1</groupId>
                            <artifactId>chassis-dependencies</artifactId>
                        </dependency>
                %s    </dependencies>
                </project>
                """.formatted(chassisVersion, extraDependencies);
    }

    private static String rulesPom(String chassisVersion) {
        return """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.example</groupId>
                    <artifactId>rules</artifactId>
                    <version>1.0.0</version>
                    <properties>
                        <chassis.version>%s</chassis.version>
                        <corral.version>0.6.1</corral.version>
                    </properties>
                    <dependencyManagement>
                        <dependencies>
                            <dependency>
                                <groupId>io.github.milczekt1</groupId>
                                <artifactId>chassis-bom</artifactId>
                                <version>${chassis.version}</version>
                                <type>pom</type>
                                <scope>import</scope>
                            </dependency>
                        </dependencies>
                    </dependencyManagement>
                    <dependencies>
                        <dependency>
                            <groupId>io.github.milczekt1</groupId>
                            <artifactId>corral-rules</artifactId>
                            <version>${corral.version}</version>
                        </dependency>
                    </dependencies>
                </project>
                """.formatted(chassisVersion);
    }
}
