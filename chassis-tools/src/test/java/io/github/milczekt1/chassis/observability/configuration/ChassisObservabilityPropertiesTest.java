package io.github.milczekt1.chassis.observability.configuration;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ChassisObservabilityPropertiesTest {

    @Test
    void shouldHaveCorrectDefaults() {
        // given
        ChassisObservabilityProperties properties = new ChassisObservabilityProperties();

        // then
        assertThat(properties.isEnabled()).isTrue();

        // Metrics defaults
        final var metrics = properties.getMetrics();
        assertThat(metrics.isEnabled()).isTrue();
        assertThat(metrics.getExport().getOtlp().isEnabled()).isTrue();
        assertThat(metrics.getExport().getOtlp().getEndpoint())
                .isEqualTo("http://localhost:4318/v1/metrics");
        assertThat(metrics.getExport().getOtlp().getProtocol())
                .isEqualTo("http/protobuf");
        assertThat(metrics.getExport().getOtlp().getStep())
                .isEqualTo(Duration.ofSeconds(10));

        // Traces defaults
        assertThat(properties.getTraces().isEnabled()).isTrue();
        assertThat(properties.getTraces().getExport().getOtlp().isEnabled()).isTrue();
        assertThat(properties.getTraces().getExport().getOtlp().getEndpoint())
                .isEqualTo("http://localhost:4318/v1/traces");

        // Logs defaults
        assertThat(properties.getLogs().isEnabled()).isTrue();
        assertThat(properties.getLogs().getExport().getOtlp().isEnabled()).isTrue();
        assertThat(properties.getLogs().getExport().getOtlp().getEndpoint())
                .isEqualTo("http://localhost:4318/v1/logs");

        // Authentication defaults
        assertThat(properties.getAuthentication().getType())
                .isEqualTo(ChassisObservabilityProperties.Authentication.Type.BASIC);
        assertThat(properties.getAuthentication().getBasic().getUsername()).isNull();
        assertThat(properties.getAuthentication().getBasic().getPassword()).isNull();
        assertThat(properties.getAuthentication().getHeader().getName()).isEqualTo("Authorization");
        assertThat(properties.getAuthentication().getHeader().getValue()).isNull();

        // Resource attributes and common tags should be empty by default
        assertThat(properties.getResourceAttributes()).isEmpty();
        assertThat(properties.getCommonTags()).isEmpty();
    }

    @Test
    void shouldHandleEmptyResourceAttributes() {
        // given
        ChassisObservabilityProperties properties = new ChassisObservabilityProperties();

        // then
        assertThat(properties.getResourceAttributes()).isNotNull();
        assertThat(properties.getResourceAttributes()).isEmpty();
    }

    @Test
    void shouldHandleEmptyCommonTags() {
        // given
        ChassisObservabilityProperties properties = new ChassisObservabilityProperties();

        // then
        assertThat(properties.getCommonTags()).isNotNull();
        assertThat(properties.getCommonTags()).isEmpty();
    }

    @Nested
    class Binding {


        @Test
        void shouldBindPropertiesCorrectly() {
            // given
            Map<String, String> properties = new HashMap<>();
            properties.put("chassis.observability.enabled", "false");
            properties.put("chassis.observability.metrics.enabled", "false");
            properties.put("chassis.observability.metrics.export.otlp.endpoint", "https://custom-endpoint.com/metrics");
            properties.put("chassis.observability.metrics.export.otlp.protocol", "grpc");
            properties.put("chassis.observability.metrics.export.otlp.step", "30s");
            properties.put("chassis.observability.authentication.type", "HEADER");
            properties.put("chassis.observability.authentication.basic.username", "test-user");
            properties.put("chassis.observability.authentication.basic.password", "test-pass");
            properties.put("chassis.observability.authentication.header.name", "X-API-Key");
            properties.put("chassis.observability.authentication.header.value", "secret-key");
            properties.put("chassis.observability.resource-attributes.service.name", "my-service");
            properties.put("chassis.observability.resource-attributes.service.namespace", "my-namespace");
            properties.put("chassis.observability.resource-attributes.deployment.environment", "production");
            properties.put("chassis.observability.common-tags.application", "my-app");
            properties.put("chassis.observability.common-tags.team", "platform");

            ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
            Binder binder = new Binder(source);

            // when
            ChassisObservabilityProperties bound = binder.bind("chassis.observability", ChassisObservabilityProperties.class).get();

            // then
            assertThat(bound.isEnabled()).isFalse();
            assertThat(bound.getMetrics().isEnabled()).isFalse();
            assertThat(bound.getMetrics().getExport().getOtlp().getEndpoint())
                    .isEqualTo("https://custom-endpoint.com/metrics");
            assertThat(bound.getMetrics().getExport().getOtlp().getProtocol()).isEqualTo("grpc");
            assertThat(bound.getMetrics().getExport().getOtlp().getStep()).isEqualTo(Duration.ofSeconds(30));

            assertThat(bound.getAuthentication().getType())
                    .isEqualTo(ChassisObservabilityProperties.Authentication.Type.HEADER);
            assertThat(bound.getAuthentication().getBasic().getUsername()).isEqualTo("test-user");
            assertThat(bound.getAuthentication().getBasic().getPassword()).isEqualTo("test-pass");
            assertThat(bound.getAuthentication().getHeader().getName()).isEqualTo("X-API-Key");
            assertThat(bound.getAuthentication().getHeader().getValue()).isEqualTo("secret-key");

            assertThat(bound.getResourceAttributes())
                    .containsEntry("service.name", "my-service")
                    .containsEntry("service.namespace", "my-namespace")
                    .containsEntry("deployment.environment", "production");

            assertThat(bound.getCommonTags())
                    .containsEntry("application", "my-app")
                    .containsEntry("team", "platform");
        }

        @Test
        void shouldBindTracesPropertiesCorrectly() {
            // given
            Map<String, String> properties = new HashMap<>();
            properties.put("chassis.observability.traces.enabled", "false");
            properties.put("chassis.observability.traces.export.otlp.endpoint", "https://traces-endpoint.com");
            properties.put("chassis.observability.traces.export.otlp.protocol", "grpc");

            ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
            Binder binder = new Binder(source);

            // when
            ChassisObservabilityProperties bound = binder.bind("chassis.observability", ChassisObservabilityProperties.class).get();

            // then
            assertThat(bound.getTraces().isEnabled()).isFalse();
            assertThat(bound.getTraces().getExport().getOtlp().getEndpoint())
                    .isEqualTo("https://traces-endpoint.com");
            assertThat(bound.getTraces().getExport().getOtlp().getProtocol()).isEqualTo("grpc");
        }

        @Test
        void shouldBindLogsPropertiesCorrectly() {
            // given
            Map<String, String> properties = new HashMap<>();
            properties.put("chassis.observability.logs.enabled", "false");
            properties.put("chassis.observability.logs.export.otlp.endpoint", "https://logs-endpoint.com");

            ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
            Binder binder = new Binder(source);

            // when
            ChassisObservabilityProperties bound = binder.bind("chassis.observability", ChassisObservabilityProperties.class).get();

            // then
            assertThat(bound.getLogs().isEnabled()).isFalse();
            assertThat(bound.getLogs().getExport().getOtlp().getEndpoint())
                    .isEqualTo("https://logs-endpoint.com");
        }

    }
}
