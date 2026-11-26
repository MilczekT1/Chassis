package io.github.milczekt1.chassis.rewrite;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import java.nio.file.Path;

import static io.github.milczekt1.chassis.rewrite.ChassisTestRepository.LEGACY;
import static io.github.milczekt1.chassis.rewrite.ChassisTestRepository.target;
import static io.github.milczekt1.chassis.rewrite.TestPoms.REST_CLIENT;
import static io.github.milczekt1.chassis.rewrite.TestPoms.dependency;
import static io.github.milczekt1.chassis.rewrite.TestPoms.modulePom;
import static org.openrewrite.java.Assertions.java;
import static org.openrewrite.java.Assertions.mavenProject;
import static org.openrewrite.java.Assertions.srcMainJava;
import static org.openrewrite.java.Assertions.srcTestJava;
import static org.openrewrite.maven.Assertions.pomXml;

class AddSpringBootRestclientWhereUsedTest implements RewriteTest {

    @TempDir
    Path repository;

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipeFromResources("io.github.milczekt1.chassis.AddSpringBootRestclientWhereUsed")
                .executionContext(ChassisTestRepository.executionContext(repository, LEGACY, target()))
                .parser(JavaParser.fromJavaVersion().classpath("spring-boot-restclient", "jspecify"));
    }

    @Test
    void addsAtCompileScopeWhereMainCodeUses() {
        rewriteRun(mavenProject("app",
                pomXml(modulePom("app", target(), ""),
                        modulePom("app", target(), dependency("org.springframework.boot", "spring-boot-restclient", null), "")),
                srcMainJava(java(REST_CLIENT))));
    }

    @Test
    void addsAtTestScopeWhereOnlyTestsUse() {
        rewriteRun(mavenProject("app",
                pomXml(modulePom("app", target(), ""),
                        modulePom("app", target(), dependency("org.springframework.boot", "spring-boot-restclient", "test"))),
                srcTestJava(java(REST_CLIENT))));
    }

    @Test
    void leavesModuleWithoutRestclientUsage() {
        rewriteRun(mavenProject("app",
                pomXml(modulePom("app", target(), "")),
                srcMainJava(java("package com.example;\n\nclass Plain {\n}\n"))));
    }

    @Test
    void leavesModuleThatStillGetsItFromChassis() {
        rewriteRun(mavenProject("app",
                pomXml(modulePom("app", LEGACY, "")),
                srcMainJava(java(REST_CLIENT))));
    }
}
