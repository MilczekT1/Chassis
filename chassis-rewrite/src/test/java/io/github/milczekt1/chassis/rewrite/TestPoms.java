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
}
