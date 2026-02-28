package io.github.milczekt1.chassis.test.observability;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

@TestConfiguration
public class ObservabilityTestConfiguration {

    public static final String TAG_APPLICATION_KEY = "application";
    public static final String TAG_APPLICATION_VALUE = "chassis-tools-test";

    @Bean
    public MeterRegistry meterRegistry() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();

        // Apply common tags that would normally come from ChassisObservabilityAutoConfiguration
        registry.config().commonTags(List.of(
                Tag.of(TAG_APPLICATION_KEY, TAG_APPLICATION_VALUE)
        ));
        return registry;
    }
}
