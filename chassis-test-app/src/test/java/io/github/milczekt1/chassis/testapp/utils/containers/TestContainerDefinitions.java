package io.github.milczekt1.chassis.testapp.utils.containers;

import org.testcontainers.containers.MongoDBContainer;

public final class TestContainerDefinitions {

    public static final String MONGODB_DB_NAME = "testDatabase";

    public static final MongoDBContainer MONGODB = new MongoDBContainer("mongo:8.0.15")
            .withEnv("MONGO_INITDB_DATABASE", MONGODB_DB_NAME)
            .withReuse(true);
}
