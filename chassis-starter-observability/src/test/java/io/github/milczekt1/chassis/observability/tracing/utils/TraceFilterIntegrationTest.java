package io.github.milczekt1.chassis.observability.tracing.utils;

import io.github.milczekt1.chassis.observability.configuration.ChassisObservabilityAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
@SpringBootTest(classes = TracingTestApp.class)
@ImportAutoConfiguration(ChassisObservabilityAutoConfiguration.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "chassis.observability.enabled=true",
        "chassis.observability.traces.enabled=true",
        "chassis.observability.traces.exposeHeaders=true"
})
public @interface TraceFilterIntegrationTest {
}
