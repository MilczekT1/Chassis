package io.github.milczekt1.chassis.test.containers;

import org.springframework.boot.jdbc.autoconfigure.JdbcConnectionDetails;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Do not expose the container as a bean: Spring closes container beans with their context, which stops the container
 * for every other cached context sharing it.
 */
@TestConfiguration(proxyBeanMethods = false)
class PostgresContainerConfiguration {

    private static final Map<String, PostgreSQLContainer> CONTAINERS_BY_IMAGE = new ConcurrentHashMap<>();

    @Bean
    JdbcConnectionDetails postgresConnectionDetails(Environment environment) {
        var image = environment.getProperty("chassis.test.postgres.image", "postgres:17.12-alpine");
        var container = CONTAINERS_BY_IMAGE.computeIfAbsent(image, PostgresContainerConfiguration::start);
        return new JdbcConnectionDetails() {
            @Override
            public String getUsername() {
                return container.getUsername();
            }

            @Override
            public String getPassword() {
                return container.getPassword();
            }

            @Override
            public String getJdbcUrl() {
                return container.getJdbcUrl();
            }
        };
    }

    // Left open for the JVM's lifetime; Testcontainers removes the container when the JVM exits.
    @SuppressWarnings("java:S2095")
    private static PostgreSQLContainer start(String image) {
        var container = new PostgreSQLContainer(DockerImageName.parse(image))
                .withReuse(System.getenv("CI") == null);
        container.start();
        return container;
    }
}
