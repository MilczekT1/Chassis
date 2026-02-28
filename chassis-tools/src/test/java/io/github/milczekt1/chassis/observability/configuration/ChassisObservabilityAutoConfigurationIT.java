package io.github.milczekt1.chassis.observability.configuration;

import io.github.milczekt1.chassis.observability.tracing.ChassisTraceResponseHeaderFilter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChassisObservabilityAutoConfigurationIT {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ChassisObservabilityAutoConfiguration.class));

    @Test
    void whenDependenciesPresent_thenAutoConfigurationActivates() {
        contextRunner
                .run(context -> {
                    assertThat(context).hasSingleBean(ChassisObservabilityAutoConfiguration.class);
                    assertThat(context).hasSingleBean(ChassisObservabilityProperties.class);
                });
    }

    @Test
    void whenEnabled_thenMeterRegistryCustomizerCreated() {
        contextRunner
                .withPropertyValues("chassis.observability.enabled=true")
                .run(context -> {
                    assertThat(context).hasBean("chassisObservabilityMeterRegistryCustomizer");
                    assertThat(context).getBean("chassisObservabilityMeterRegistryCustomizer")
                            .isInstanceOf(MeterRegistryCustomizer.class);
                });
    }

    @Test
    void whenDisabled_thenAutoConfigurationNotActivated() {
        contextRunner
                .withPropertyValues("chassis.observability.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(ChassisObservabilityAutoConfiguration.class);
                    assertThat(context).doesNotHaveBean("chassisObservabilityMeterRegistryCustomizer");
                });
    }

    @Test
    void whenCommonTagsConfigured_thenAppliedToMeterRegistry() {
        contextRunner
                .withPropertyValues(
                        "chassis.observability.enabled=true",
                        "chassis.observability.common-tags.application=test-app",
                        "chassis.observability.common-tags.team=platform"
                )
                .run(context -> {
                    MeterRegistryCustomizer<MeterRegistry> customizer =
                            (MeterRegistryCustomizer<MeterRegistry>) context.getBean("chassisObservabilityMeterRegistryCustomizer");
                    SimpleMeterRegistry registry = new SimpleMeterRegistry();
                    customizer.customize(registry);

                    // Verify common tags are applied by creating a meter and checking its tags
                    var counter = registry.counter("test.counter");
                    List<Tag> meterTags = new ArrayList<>();
                    counter.getId().getTags().forEach(meterTags::add);

                    assertThat(meterTags).anyMatch(tag -> tag.getKey().equals("application") && tag.getValue().equals("test-app"));
                    assertThat(meterTags).anyMatch(tag -> tag.getKey().equals("team") && tag.getValue().equals("platform"));
                });
    }

    @Test
    void whenResourceAttributesConfigured_thenAppliedToMeterRegistry() {
        contextRunner
                .withPropertyValues(
                        "chassis.observability.enabled=true",
                        "chassis.observability.resource-attributes.service.name=my-service",
                        "chassis.observability.resource-attributes.service.namespace=my-namespace",
                        "chassis.observability.resource-attributes.deployment.environment=production"
                )
                .run(context -> {
                    MeterRegistryCustomizer<MeterRegistry> customizer =
                            (MeterRegistryCustomizer<MeterRegistry>) context.getBean("chassisObservabilityMeterRegistryCustomizer");
                    SimpleMeterRegistry registry = new SimpleMeterRegistry();
                    customizer.customize(registry);

                    // Verify resource attributes are applied as tags by creating a meter
                    var counter = registry.counter("test.counter");
                    List<Tag> meterTags = new ArrayList<>(counter.getId().getTags());

                    assertThat(meterTags).anyMatch(tag -> tag.getKey().equals("service.name") && tag.getValue().equals("my-service"));
                    assertThat(meterTags).anyMatch(tag -> tag.getKey().equals("service.namespace") && tag.getValue().equals("my-namespace"));
                    assertThat(meterTags).anyMatch(tag -> tag.getKey().equals("deployment.environment") && tag.getValue().equals("production"));
                });
    }

    @Test
    void whenBothCommonTagsAndResourceAttributes_thenBothApplied() {
        contextRunner
                .withPropertyValues(
                        "chassis.observability.enabled=true",
                        "chassis.observability.common-tags.application=test-app",
                        "chassis.observability.resource-attributes.service.name=my-service"
                )
                .run(context -> {
                    MeterRegistryCustomizer<MeterRegistry> customizer =
                            (MeterRegistryCustomizer<MeterRegistry>) context.getBean("chassisObservabilityMeterRegistryCustomizer");
                    SimpleMeterRegistry registry = new SimpleMeterRegistry();
                    customizer.customize(registry);

                    // Both common tags and resource attributes should be present
                    var counter = registry.counter("test.counter");
                    List<Tag> meterTags = new ArrayList<>(counter.getId().getTags());

                    assertThat(meterTags).anyMatch(tag -> tag.getKey().equals("application") && tag.getValue().equals("test-app"));
                    assertThat(meterTags).anyMatch(tag -> tag.getKey().equals("service.name") && tag.getValue().equals("my-service"));
                });
    }

    @Test
    void whenEmptyTagValues_thenFilteredOut() {
        contextRunner
                .withPropertyValues(
                        "chassis.observability.enabled=true",
                        "chassis.observability.common-tags.application=test-app",
                        "chassis.observability.common-tags.empty=",
                        "chassis.observability.resource-attributes.service.name=my-service"
                )
                .run(context -> {
                    MeterRegistryCustomizer<MeterRegistry> customizer =
                            (MeterRegistryCustomizer<MeterRegistry>) context.getBean("chassisObservabilityMeterRegistryCustomizer");
                    SimpleMeterRegistry registry = new SimpleMeterRegistry();
                    customizer.customize(registry);

                    // Empty values should be filtered out
                    var counter = registry.counter("test.counter");
                    List<Tag> meterTags = new ArrayList<>(counter.getId().getTags());

                    assertThat(meterTags).noneMatch(tag -> tag.getKey().equals("empty"));
                    assertThat(meterTags).anyMatch(tag -> tag.getKey().equals("application") && tag.getValue().equals("test-app"));
                    assertThat(meterTags).anyMatch(tag -> tag.getKey().equals("service.name") && tag.getValue().equals("my-service"));
                });
    }

    @Test
    void whenTracingEnabled_thenChassisTraceResponseHeaderFilterCreated() {
        contextRunner
                .withPropertyValues(
                        "chassis.observability.enabled=true",
                        "chassis.observability.traces.enabled=true",
                        "chassis.observability.traces.exposeHeaders=true"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(ChassisTraceResponseHeaderFilter.class);
                });
    }

    @Test
    void whenTracingDisabled_thenChassisTraceResponseHeaderFilterNotCreated() {
        contextRunner
                .withPropertyValues(
                        "chassis.observability.enabled=true",
                        "chassis.observability.traces.enabled=false"
                )
                .run(context -> {
                    assertThat(context).doesNotHaveBean(ChassisTraceResponseHeaderFilter.class);
                });
    }

    @Test
    void whenExposeHeadersDisabled_thenChassisTraceResponseHeaderFilterNotCreated() {
        contextRunner
                .withPropertyValues(
                        "chassis.observability.enabled=true",
                        "chassis.observability.traces.enabled=true",
                        "chassis.observability.traces.exposeHeaders=false"
                )
                .run(context -> {
                    assertThat(context).doesNotHaveBean(ChassisTraceResponseHeaderFilter.class);
                });
    }

    @Test
    void whenPropertiesNotSet_thenDefaultsApplied() {
        contextRunner
                .run(context -> {
                    ChassisObservabilityProperties properties = context.getBean(ChassisObservabilityProperties.class);

                    // Verify defaults
                    assertThat(properties.isEnabled()).isTrue();
                    assertThat(properties.getMetrics().isEnabled()).isTrue();
                    assertThat(properties.getMetrics().getExport().getOtlp().getEndpoint())
                            .isEqualTo("http://localhost:4318/v1/metrics");
                    assertThat(properties.getTraces().isEnabled()).isTrue();
                    assertThat(properties.getTraces().isExposeHeaders()).isTrue();
                    assertThat(properties.getTraces().getSampling().getRate()).isEqualTo(1.0);
                });
    }

    @Test
    void whenSamplingConfigured_thenPropertiesApplied() {
        contextRunner
                .withPropertyValues(
                        "chassis.observability.traces.sampling.rate=0.5",
                        "chassis.observability.traces.sampling.strategy=ALWAYS_ON"
                )
                .run(context -> {
                    ChassisObservabilityProperties properties = context.getBean(ChassisObservabilityProperties.class);

                    assertThat(properties.getTraces().getSampling().getRate()).isEqualTo(0.5);
                    assertThat(properties.getTraces().getSampling().getStrategy())
                            .isEqualTo(ChassisObservabilityProperties.Traces.Sampling.Strategy.ALWAYS_ON);
                });
    }

}
