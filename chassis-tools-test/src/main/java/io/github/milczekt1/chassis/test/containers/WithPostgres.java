package io.github.milczekt1.chassis.test.containers;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Points the test's DataSource at a PostgreSQL container. One container per image is started on first use and shared
 * by every test context in the JVM; it runs until the JVM exits.
 * <p>
 * The image is {@code postgres:17.12-alpine} unless the {@code chassis.test.postgres.image} property sets another.
 * Requires {@code org.testcontainers:testcontainers-postgresql} on the test classpath.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import(PostgresContainerConfiguration.class)
public @interface WithPostgres {
}
