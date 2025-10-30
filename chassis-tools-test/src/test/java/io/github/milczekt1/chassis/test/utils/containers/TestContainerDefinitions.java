package io.github.milczekt1.chassis.test.utils.containers;

import org.testcontainers.containers.MongoDBContainer;

import java.time.Duration;

public final class TestContainerDefinitions {

    public static final String MONGODB_DB_NAME = "testDatabase";

    public static final MongoDBContainer MONGODB = new MongoDBContainer("mongo:8.0.15")
            .withEnv("MONGO_INITDB_DATABASE", MONGODB_DB_NAME)
            .withReuse(System.getenv("CI") == null)
            .withStartupTimeout(Duration.ofMinutes(2));
}
