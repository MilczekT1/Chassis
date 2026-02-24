package io.github.milczekt1.chassis.test.utils;

import io.github.milczekt1.chassis.test.utils.containers.TestContainerDefinitions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
@ActiveProfiles({"itest"})
@Import(TestContainerDefinitions.class)
@SpringBootTest(classes = TestApp.class)
public @interface TestToolsIntegrationTest {
}
