package io.github.milczekt1.chassis.test.containers;

import com.mongodb.ConnectionString;
import org.springframework.boot.mongodb.autoconfigure.MongoConnectionDetails;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Do not expose the container as a bean: Spring closes container beans with their context, which stops the container
 * for every other cached context sharing it.
 */
@TestConfiguration(proxyBeanMethods = false)
class MongoContainerConfiguration {

    private static final Map<String, MongoDBContainer> CONTAINERS_BY_IMAGE = new ConcurrentHashMap<>();

    @Bean
    MongoConnectionDetails mongoConnectionDetails(Environment environment) {
        var image = environment.getProperty("chassis.test.mongo.image", "mongo:8.0.15");
        var container = CONTAINERS_BY_IMAGE.computeIfAbsent(image, MongoContainerConfiguration::start);
        return () -> new ConnectionString(container.getReplicaSetUrl());
    }

    // Left open for the JVM's lifetime; Testcontainers removes the container when the JVM exits.
    @SuppressWarnings("java:S2095")
    private static MongoDBContainer start(String image) {
        var container = new MongoDBContainer(DockerImageName.parse(image))
                .withReuse(System.getenv("CI") == null);
        container.start();
        return container;
    }
}
