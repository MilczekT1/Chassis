package io.github.milczekt1.chassis.rewrite;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import java.nio.file.Path;

import static io.github.milczekt1.chassis.rewrite.ChassisTestRepository.LEGACY;
import static io.github.milczekt1.chassis.rewrite.ChassisTestRepository.target;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openrewrite.maven.Assertions.pomXml;

class UpgradeChassisVersionTest implements RewriteTest {

    @TempDir
    Path repository;

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new UpgradeChassisVersion(target()))
                .executionContext(ChassisTestRepository.executionContext(repository, LEGACY, target(), "99.0.0"));
    }

    @Test
    void upgradeChassisTargetsTheModuleVersion() {
        assertTrue(target().matches("\\d+\\.\\d+\\.\\d+(-SNAPSHOT)?"), () -> "target() is " + target());
    }

    @Test
    void bumpsChassisParent() {
        rewriteRun(pomXml(servicePom(LEGACY), servicePom(target())));
    }

    @Test
    void bumpsChassisVersionPropertyUsedByBomImport() {
        rewriteRun(pomXml(propertyPom(LEGACY), propertyPom(target())));
    }

    @Test
    void bumpsExplicitChassisDependencyVersion() {
        rewriteRun(pomXml(explicitVersionPom(LEGACY), explicitVersionPom(target())));
    }

    @Test
    void leavesProjectParentVersionReference() {
        rewriteRun(pomXml(parentVersionReferencePom(LEGACY), parentVersionReferencePom(target())));
    }

    @Test
    void leavesExplicitVersionsOfOtherDependencies() {
        rewriteRun(pomXml(pinnedDependencyPom(LEGACY), pinnedDependencyPom(target())));
    }

    @Test
    void neverLowersVersion() {
        rewriteRun(pomXml(servicePom("99.0.0")));
    }

    @Test
    void changesNothingAtTargetVersion() {
        rewriteRun(pomXml(servicePom(target())));
    }

    private static String servicePom(String chassisVersion) {
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
                    <artifactId>service</artifactId>
                    <version>1.0.0</version>
                    <dependencies>
                        <dependency>
                            <groupId>io.github.milczekt1</groupId>
                            <artifactId>chassis-dependencies</artifactId>
                        </dependency>
                    </dependencies>
                </project>
                """.formatted(chassisVersion);
    }

    private static String pinnedDependencyPom(String chassisVersion) {
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
                    <artifactId>service</artifactId>
                    <version>1.0.0</version>
                    <dependencies>
                        <dependency>
                            <groupId>org.testcontainers</groupId>
                            <artifactId>testcontainers-mongodb</artifactId>
                            <version>2.0.5</version>
                            <scope>test</scope>
                        </dependency>
                    </dependencies>
                </project>
                """.formatted(chassisVersion);
    }

    private static String propertyPom(String chassisVersion) {
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

    private static String explicitVersionPom(String chassisVersion) {
        return """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.example</groupId>
                    <artifactId>api</artifactId>
                    <version>1.0.0</version>
                    <dependencyManagement>
                        <dependencies>
                            <dependency>
                                <groupId>io.github.milczekt1</groupId>
                                <artifactId>chassis-bom</artifactId>
                                <version>%1$s</version>
                                <type>pom</type>
                                <scope>import</scope>
                            </dependency>
                        </dependencies>
                    </dependencyManagement>
                    <dependencies>
                        <dependency>
                            <groupId>io.github.milczekt1</groupId>
                            <artifactId>chassis-tools-test</artifactId>
                            <version>%1$s</version>
                            <scope>test</scope>
                        </dependency>
                    </dependencies>
                </project>
                """.formatted(chassisVersion);
    }

    private static String parentVersionReferencePom(String chassisVersion) {
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
                    </dependencies>
                </project>
                """.formatted(chassisVersion);
    }
}
