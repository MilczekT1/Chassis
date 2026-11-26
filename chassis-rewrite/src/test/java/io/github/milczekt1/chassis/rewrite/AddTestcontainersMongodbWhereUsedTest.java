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
import static io.github.milczekt1.chassis.rewrite.TestPoms.dependency;
import static io.github.milczekt1.chassis.rewrite.TestPoms.modulePom;
import static org.openrewrite.java.Assertions.java;
import static org.openrewrite.java.Assertions.mavenProject;
import static org.openrewrite.java.Assertions.srcTestJava;
import static org.openrewrite.maven.Assertions.pomXml;

class AddTestcontainersMongodbWhereUsedTest implements RewriteTest {

    private static final String TESTCONTAINERS_MONGODB = dependency("org.testcontainers", "testcontainers-mongodb", "test");

    @TempDir
    Path repository;

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipeFromResources("io.github.milczekt1.chassis.AddTestcontainersMongodbWhereUsed")
                .executionContext(ChassisTestRepository.executionContext(repository, LEGACY, target()))
                .parser(JavaParser.fromJavaVersion().classpath("testcontainers", "testcontainers-mongodb"));
    }

    @Test
    void addsAtTestScopeWhereTestsUseMongoDbContainer() {
        rewriteRun(mavenProject("app",
                pomXml(modulePom("app", target(), ""), modulePom("app", target(), TESTCONTAINERS_MONGODB)),
                srcTestJava(java(MONGO_TEST))));
    }

    @Test
    void addsWhereTestsUseLegacyMongoDbContainer() {
        rewriteRun(mavenProject("app",
                pomXml(modulePom("app", target(), ""), modulePom("app", target(), TESTCONTAINERS_MONGODB)),
                srcTestJava(java(MONGO_TEST.replace("org.testcontainers.mongodb.", "org.testcontainers.containers.")))));
    }

    @Test
    void leavesModuleWithoutMongoUsage() {
        rewriteRun(mavenProject("app",
                pomXml(modulePom("app", target(), "")),
                srcTestJava(java("package com.example;\n\nclass PlainTest {\n}\n"))));
    }

    @Test
    void leavesModuleThatDeclaresIt() {
        rewriteRun(mavenProject("app",
                pomXml(modulePom("app", target(), TESTCONTAINERS_MONGODB)),
                srcTestJava(java(MONGO_TEST))));
    }

    @Test
    void leavesModuleThatStillGetsItFromChassis() {
        rewriteRun(mavenProject("app",
                pomXml(modulePom("app", LEGACY, "")),
                srcTestJava(java(MONGO_TEST))));
    }

    @Test
    void onlyAddsToTheModuleThatUsesIt() {
        rewriteRun(
                mavenProject("app",
                        pomXml(modulePom("app", target(), ""), modulePom("app", target(), TESTCONTAINERS_MONGODB)),
                        srcTestJava(java(MONGO_TEST))),
                mavenProject("api",
                        pomXml(modulePom("api", target(), ""))));
    }
}
