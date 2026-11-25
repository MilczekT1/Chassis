package io.github.milczekt1.chassis.test.mongo;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Points the test's MongoDB client at a MongoDB container. One container per image is started on first use and
 * shared by every test context in the JVM; it runs until the JVM exits.
 * <p>
 * The image is {@code mongo:8.0.15} unless the {@code chassis.test.mongo.image} property sets another. Requires
 * {@code org.testcontainers:testcontainers-mongodb} on the test classpath.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import(MongoContainerConfiguration.class)
public @interface WithMongo {
}
