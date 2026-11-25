package io.github.milczekt1.chassis.test.containers;

import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.mongodb.autoconfigure.MongoConnectionDetails;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

class MongoContainerConfigurationIT {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(MongoContainerConfiguration.class);

    @Test
    void sharesOneContainerAcrossContexts() {
        contextRunner.run(first -> contextRunner.run(second ->
                assertThat(connectionString(second)).isEqualTo(connectionString(first))));
    }

    @Test
    void keepsContainerRunningWhenAnotherContextCloses() {
        contextRunner.run(first -> {
            contextRunner.run(second -> assertThat(ping(second)).isEqualTo(1.0));
            assertThat(ping(first)).isEqualTo(1.0);
        });
    }

    @Test
    void createsContainerFromImageProperty() {
        contextRunner.run(defaultImage -> contextRunner
                .withPropertyValues("chassis.test.mongo.image=example/not-mongo:1")
                .run(overridden -> assertThat(overridden).getFailure()
                        .rootCause()
                        .hasMessageContaining("example/not-mongo:1")));
    }

    private static String connectionString(ApplicationContext context) {
        return context.getBean(MongoConnectionDetails.class).getConnectionString().getConnectionString();
    }

    private static Object ping(ApplicationContext context) {
        try (var client = MongoClients.create(context.getBean(MongoConnectionDetails.class).getConnectionString())) {
            return client.getDatabase("admin").runCommand(new Document("ping", 1)).get("ok");
        }
    }
}
