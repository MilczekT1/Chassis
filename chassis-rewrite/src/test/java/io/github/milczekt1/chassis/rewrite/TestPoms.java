package io.github.milczekt1.chassis.rewrite;

import org.jspecify.annotations.Nullable;

final class TestPoms {

    static final String MONGO_TEST = """
            package com.example;

            import org.testcontainers.mongodb.MongoDBContainer;

            class MongoIT {
                MongoDBContainer mongo;
            }
            """;

    static final String REST_CLIENT = """
            package com.example;

            import org.springframework.boot.restclient.RestTemplateBuilder;

            class Client {
                RestTemplateBuilder builder;
            }
            """;

    static final String CONTRACT_BASE = """
            package com.example;

            import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;

            @AutoConfigureMessageVerifier
            abstract class ContractBase {
            }
            """;

    static final String STUB_RUNNER_TEST = """
            package com.example;

            import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;

            @AutoConfigureStubRunner
            class ClientIT {
            }
            """;

    /**
     * Stand-ins for the Spring Cloud Contract types {@link #CONTRACT_BASE} and {@link #STUB_RUNNER_TEST} use, for
     * {@code JavaParser.Builder.dependsOn}.
     */
    static final String[] CONTRACT_TYPES = {
            "package org.springframework.cloud.contract.verifier.messaging.boot; public @interface AutoConfigureMessageVerifier {}",
            "package org.springframework.cloud.contract.stubrunner.spring; public @interface AutoConfigureStubRunner {}",
            "package org.springframework.cloud.contract.spec.internal; public class RegexPatterns {}"
    };

    private TestPoms() {
    }

    /**
     * A module with {@code chassis-parent} as parent, {@code chassis-dependencies} and {@code chassis-tools-test};
     * {@code testDependencies} go last in {@code <dependencies>}.
     */
    static String modulePom(String artifactId, String chassisVersion, String testDependencies) {
        return modulePom(artifactId, chassisVersion, "", testDependencies);
    }

    /**
     * As {@link #modulePom(String, String, String)}, with {@code compileDependencies} right after
     * {@code chassis-dependencies}, where OpenRewrite inserts a compile-scoped dependency.
     */
    static String modulePom(String artifactId, String chassisVersion, String compileDependencies, String testDependencies) {
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
                    <artifactId>%s</artifactId>
                    <version>1.0.0</version>
                    <dependencies>
                        <dependency>
                            <groupId>io.github.milczekt1</groupId>
                            <artifactId>chassis-dependencies</artifactId>
                        </dependency>
                %s        <dependency>
                            <groupId>io.github.milczekt1</groupId>
                            <artifactId>chassis-tools-test</artifactId>
                            <scope>test</scope>
                        </dependency>
                %s    </dependencies>
                </project>
                """.formatted(chassisVersion, artifactId, compileDependencies, testDependencies);
    }

    /**
     * A {@code <dependency>} without a version, indented for {@link #modulePom}.
     */
    static String dependency(String groupId, String artifactId, @Nullable String scope) {
        return "        <dependency>\n" +
               "            <groupId>" + groupId + "</groupId>\n" +
               "            <artifactId>" + artifactId + "</artifactId>\n" +
               (scope == null ? "" : "            <scope>" + scope + "</scope>\n") +
               "        </dependency>\n";
    }

    /**
     * {@code pom} with the {@code spring-cloud-contract-dependencies} import OpenRewrite adds right before its
     * {@code <dependencies>}.
     */
    static String withContractBom(String pom) {
        String withImport = """
                <dependencyManagement>
                    <dependencies>
                        <dependency>
                            <groupId>org.springframework.cloud</groupId>
                            <artifactId>spring-cloud-contract-dependencies</artifactId>
                            <version>%s</version>
                            <type>pom</type>
                            <scope>import</scope>
                        </dependency>
                    </dependencies>
                </dependencyManagement>
                <dependencies>""".formatted(ChassisTestRepository.SPRING_CLOUD_CONTRACT).indent(4).stripTrailing();
        return pom.replaceFirst("(?m)^    <dependencies>$", withImport);
    }
}
