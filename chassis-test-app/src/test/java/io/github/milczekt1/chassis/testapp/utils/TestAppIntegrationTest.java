package io.github.milczekt1.chassis.testapp.utils;


import io.github.milczekt1.chassis.test.logging.LogbackVerifierExtension;
import io.github.milczekt1.chassis.testapp.ChassisTestApp;
import io.github.milczekt1.chassis.testapp.utils.containers.TestContainerDefinitions;
import org.junit.jupiter.api.extension.ExtendWith;
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
@ExtendWith(LogbackVerifierExtension.class)
@Import(TestContainerDefinitions.class)
@SpringBootTest(classes = ChassisTestApp.class)
public @interface TestAppIntegrationTest {
}
