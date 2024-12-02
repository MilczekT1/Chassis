package io.github.milczekt1.chassis.exceptions.utils;

import io.github.milczekt1.chassis.exceptions.configuration.ChassisExceptionHandlingAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
@ActiveProfiles({"itest"})
@SpringBootTest(classes = ExceptionHandlerTestApp.class)
@ImportAutoConfiguration(ChassisExceptionHandlingAutoConfiguration.class)
@AutoConfigureMockMvc
public @interface ChassisErrorHandlerIntegrationTest {
}
