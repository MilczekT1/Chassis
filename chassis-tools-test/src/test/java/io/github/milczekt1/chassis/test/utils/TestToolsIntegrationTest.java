package io.github.milczekt1.chassis.test.utils;

import io.github.milczekt1.chassis.test.mongo.WithMongo;
import io.github.milczekt1.chassis.test.jdbc.WithPostgres;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
@ActiveProfiles({"itest"})
@WithMongo
@WithPostgres
@SpringBootTest(classes = TestApp.class)
public @interface TestToolsIntegrationTest {
}
