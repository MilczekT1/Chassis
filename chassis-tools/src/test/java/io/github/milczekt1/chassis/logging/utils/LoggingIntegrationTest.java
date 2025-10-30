package io.github.milczekt1.chassis.logging.utils;

import io.github.milczekt1.chassis.logging.configuration.ChassisLoggingAutoConfiguration;
import io.github.milczekt1.chassis.test.logging.LogbackVerifierExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
@ActiveProfiles({"itest"})
@ExtendWith(LogbackVerifierExtension.class)
@SpringBootTest(classes = LoggingTestApp.class)
@ImportAutoConfiguration(ChassisLoggingAutoConfiguration.class)
@AutoConfigureMockMvc
public @interface LoggingIntegrationTest {

    @AliasFor(annotation = SpringBootTest.class, attribute = "properties")
    String[] properties() default {};
}
