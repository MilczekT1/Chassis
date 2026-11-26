package io.github.milczekt1.chassis.rewrite;

import org.junit.jupiter.api.Test;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;
import org.openrewrite.test.TypeValidation;

import static org.openrewrite.java.Assertions.java;
import static org.openrewrite.test.SourceSpecs.text;

class RemoveChassisTestMongoCleanupConfigurationTest implements RewriteTest {

    private static final String IMPORTS_FILE =
            "src/test/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports";
    private static final String SPRING_FACTORIES = "src/test/resources/META-INF/spring.factories";

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new RemoveChassisTestMongoCleanupConfiguration())
                .parser(JavaParser.fromJavaVersion()
                        .classpath("spring-context", "spring-boot-autoconfigure")
                        .dependsOn(
                                "package io.github.milczekt1.chassis.test.mongo; public class ChassisTestMongoCleanupConfiguration {}",
                                "package com.example; public class OtherConfig {}"));
    }

    @Test
    void removesSoleImportedClassAndTheAnnotation() {
        rewriteRun(java(
                """
                package com.example;

                import io.github.milczekt1.chassis.test.mongo.ChassisTestMongoCleanupConfiguration;
                import org.springframework.context.annotation.Configuration;
                import org.springframework.context.annotation.Import;

                @Configuration
                @Import(ChassisTestMongoCleanupConfiguration.class)
                class MongoTestConfig {
                }
                """,
                """
                package com.example;

                import org.springframework.context.annotation.Configuration;

                @Configuration
                class MongoTestConfig {
                }
                """));
    }

    @Test
    void removesClassFromImportArray() {
        rewriteRun(java(
                """
                package com.example;

                import io.github.milczekt1.chassis.test.mongo.ChassisTestMongoCleanupConfiguration;
                import org.springframework.context.annotation.Import;

                @Import({OtherConfig.class, ChassisTestMongoCleanupConfiguration.class})
                class MongoTestConfig {
                }
                """,
                """
                package com.example;

                import org.springframework.context.annotation.Import;

                @Import({OtherConfig.class})
                class MongoTestConfig {
                }
                """));
    }

    @Test
    void removesClassWhenFirstInImportArray() {
        rewriteRun(java(
                """
                package com.example;

                import io.github.milczekt1.chassis.test.mongo.ChassisTestMongoCleanupConfiguration;
                import org.springframework.context.annotation.Import;

                @Import({ChassisTestMongoCleanupConfiguration.class, OtherConfig.class})
                class MongoTestConfig {
                }
                """,
                """
                package com.example;

                import org.springframework.context.annotation.Import;

                @Import({OtherConfig.class})
                class MongoTestConfig {
                }
                """));
    }

    @Test
    void removesClassesAttributeOfImportAutoConfiguration() {
        rewriteRun(java(
                """
                package com.example;

                import io.github.milczekt1.chassis.test.mongo.ChassisTestMongoCleanupConfiguration;
                import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

                @ImportAutoConfiguration(classes = ChassisTestMongoCleanupConfiguration.class, exclude = OtherConfig.class)
                class MongoTestConfig {
                }
                """,
                """
                package com.example;

                import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

                @ImportAutoConfiguration(exclude = OtherConfig.class)
                class MongoTestConfig {
                }
                """));
    }

    @Test
    void removesFullyQualifiedClassLiteral() {
        rewriteRun(java(
                """
                package com.example;

                import org.springframework.context.annotation.Import;

                @Import(io.github.milczekt1.chassis.test.mongo.ChassisTestMongoCleanupConfiguration.class)
                class MongoTestConfig {
                }
                """,
                """
                package com.example;

                class MongoTestConfig {
                }
                """));
    }

    @Test
    void removesClassThatNoLongerResolves() {
        rewriteRun(
                spec -> spec.parser(JavaParser.fromJavaVersion()
                                .classpath("spring-context")
                                .dependsOn("package com.example; public class OtherConfig {}"))
                        .typeValidationOptions(TypeValidation.none()),
                java(
                        """
                        package com.example;

                        import io.github.milczekt1.chassis.test.mongo.ChassisTestMongoCleanupConfiguration;
                        import org.springframework.context.annotation.Import;

                        @Import({OtherConfig.class, ChassisTestMongoCleanupConfiguration.class})
                        class MongoTestConfig {
                        }
                        """,
                        """
                        package com.example;

                        import org.springframework.context.annotation.Import;

                        @Import({OtherConfig.class})
                        class MongoTestConfig {
                        }
                        """));
    }

    @Test
    void removesFullyQualifiedClassThatNoLongerResolves() {
        rewriteRun(
                spec -> spec.parser(JavaParser.fromJavaVersion().classpath("spring-context"))
                        .typeValidationOptions(TypeValidation.none()),
                java(
                        """
                        package com.example;

                        import org.springframework.context.annotation.Import;

                        @Import(io.github.milczekt1.chassis.test.mongo.ChassisTestMongoCleanupConfiguration.class)
                        class MongoTestConfig {
                        }
                        """,
                        """
                        package com.example;

                        class MongoTestConfig {
                        }
                        """));
    }

    @Test
    void leavesOtherReferencesForTheCompiler() {
        rewriteRun(java(
                """
                package com.example;

                import io.github.milczekt1.chassis.test.mongo.ChassisTestMongoCleanupConfiguration;

                class MongoTestConfig {
                    Object config() {
                        return new ChassisTestMongoCleanupConfiguration();
                    }
                }
                """));
    }

    @Test
    void removesLineFromImportsFile() {
        rewriteRun(text(
                "com.example.OtherAutoConfiguration\n" +
                "io.github.milczekt1.chassis.test.mongo.ChassisTestMongoCleanupConfiguration\n" +
                "com.example.ThirdAutoConfiguration\n",
                "com.example.OtherAutoConfiguration\n" +
                "com.example.ThirdAutoConfiguration\n",
                spec -> spec.path(IMPORTS_FILE)));
    }

    @Test
    void removesLineFromCrlfImportsFile() {
        rewriteRun(text(
                "com.example.OtherAutoConfiguration\r\n" +
                "io.github.milczekt1.chassis.test.mongo.ChassisTestMongoCleanupConfiguration\r\n" +
                "com.example.ThirdAutoConfiguration\r\n",
                "com.example.OtherAutoConfiguration\r\n" +
                "com.example.ThirdAutoConfiguration\r\n",
                spec -> spec.path(IMPORTS_FILE)));
    }

    @Test
    void removesLastLineWithoutTrailingNewline() {
        rewriteRun(text(
                "com.example.OtherAutoConfiguration\n" +
                "io.github.milczekt1.chassis.test.mongo.ChassisTestMongoCleanupConfiguration",
                "com.example.OtherAutoConfiguration",
                spec -> spec.path(IMPORTS_FILE)));
    }

    @Test
    void removesOnlyLineWithoutTrailingNewline() {
        rewriteRun(text(
                "io.github.milczekt1.chassis.test.mongo.ChassisTestMongoCleanupConfiguration",
                "",
                spec -> spec.path(IMPORTS_FILE)));
    }

    @Test
    void removesLineWithTrailingComment() {
        rewriteRun(text(
                "com.example.OtherAutoConfiguration\n" +
                "io.github.milczekt1.chassis.test.mongo.ChassisTestMongoCleanupConfiguration # mongo cleanup\n" +
                "com.example.ThirdAutoConfiguration\n",
                "com.example.OtherAutoConfiguration\n" +
                "com.example.ThirdAutoConfiguration\n",
                spec -> spec.path(IMPORTS_FILE)));
    }

    @Test
    void removesLastValueFromSpringFactoriesList() {
        rewriteRun(text(
                "org.springframework.boot.autoconfigure.EnableAutoConfiguration=\\\n" +
                "com.example.OtherAutoConfiguration,\\\n" +
                "io.github.milczekt1.chassis.test.mongo.ChassisTestMongoCleanupConfiguration\n",
                "org.springframework.boot.autoconfigure.EnableAutoConfiguration=\\\n" +
                "com.example.OtherAutoConfiguration\n",
                spec -> spec.path(SPRING_FACTORIES)));
    }

    @Test
    void removesFirstValueFromSpringFactoriesList() {
        rewriteRun(text(
                "org.springframework.boot.autoconfigure.EnableAutoConfiguration=\\\n" +
                "io.github.milczekt1.chassis.test.mongo.ChassisTestMongoCleanupConfiguration,\\\n" +
                "com.example.OtherAutoConfiguration\n",
                "org.springframework.boot.autoconfigure.EnableAutoConfiguration=\\\n" +
                "com.example.OtherAutoConfiguration\n",
                spec -> spec.path(SPRING_FACTORIES)));
    }

    @Test
    void removesEntryWhoseOnlyValueIsTheClass() {
        rewriteRun(text(
                "org.springframework.boot.autoconfigure.EnableAutoConfiguration=" +
                "io.github.milczekt1.chassis.test.mongo.ChassisTestMongoCleanupConfiguration\n" +
                "org.springframework.test.context.TestExecutionListener=com.example.Listener\n",
                "org.springframework.test.context.TestExecutionListener=com.example.Listener\n",
                spec -> spec.path(SPRING_FACTORIES)));
    }

    @Test
    void leavesLookalikeClassNameInSpringFactories() {
        rewriteRun(text(
                "org.springframework.boot.autoconfigure.EnableAutoConfiguration=\\\n" +
                "com.example.OtherAutoConfiguration,\\\n" +
                "io.github.milczekt1.chassis.test.mongo.ChassisTestMongoCleanupConfigurationExtra\n",
                spec -> spec.path(SPRING_FACTORIES)));
    }

    @Test
    void leavesTextFilesElsewhere() {
        rewriteRun(text(
                "io.github.milczekt1.chassis.test.mongo.ChassisTestMongoCleanupConfiguration\n",
                spec -> spec.path("src/test/resources/notes.txt")));
    }
}
