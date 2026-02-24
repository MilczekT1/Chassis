package io.github.milczekt1.chassis.testapp.utils.containers;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainerDefinitions {

    public static final String MONGODB_DB_NAME = "testDatabase";

    @Bean
    @ServiceConnection
    MongoDBContainer mongoDbContainer() {
        return new MongoDBContainer(DockerImageName.parse("mongo:8.0.15"))
                .withEnv("MONGO_INITDB_DATABASE", MONGODB_DB_NAME)
                .withReuse(System.getenv("CI") == null);
    }
}
