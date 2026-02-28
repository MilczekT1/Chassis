package io.github.milczekt1.chassis.observability.configuration;

import io.github.milczekt1.chassis.observability.tracing.ChassisTraceResponseHeaderFilter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.registry.otlp.OtlpMeterRegistry;
import io.opentelemetry.api.trace.Span;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Auto-configuration for Chassis Observability features.
 * <p>
 * Automatically configures OTLP-based observability for metrics, traces, and logs export
 * to backends like Grafana Cloud. Provides zero-configuration setup with sensible defaults
 * while allowing full customization via configuration properties.
 * </p>
 * <p>
 * This autoconfiguration:
 * <ul>
 *   <li>Registers a {@link MeterRegistryCustomizer} to apply common tags to all metrics</li>
 *   <li>Configures resource attributes for service identity (service.name, service.namespace, deployment.environment)</li>
 *   <li>Bridges chassis.observability properties to Spring Boot's management.otlp properties</li>
 * </ul>
 * </p>
 * Example configuration:
 * <pre>
 * chassis:
 *   observability:
 *     enabled: true
 *     metrics:
 *       export:
 *         otlp:
 *           endpoint: ${OTLP_ENDPOINT_URL}
 *     authentication:
 *       basic:
 *         username: ${OTLP_USERNAME}
 *         password: ${OTLP_PASSWORD}
 *     resource-attributes:
 *       service.name: my-service
 *       service.namespace: my-namespace
 *       deployment.environment: production
 *     common-tags:
 *       application: my-app
 *       team: platform
 * </pre>
 *
 * @see ChassisObservabilityProperties
 */
@Slf4j
@RequiredArgsConstructor
@AutoConfiguration
@ConditionalOnClass({MeterRegistry.class, OtlpMeterRegistry.class})
@ConditionalOnProperty(
        prefix = "chassis.observability",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(ChassisObservabilityProperties.class)
public class ChassisObservabilityAutoConfiguration {

    private final ChassisObservabilityProperties properties;

    @PostConstruct
    public void init() {
        log.info("Initializing Chassis Observability Auto-Configuration");
        log.info("OTLP Metrics Export: enabled={}, endpoint={}",
                properties.getMetrics().getExport().getOtlp().isEnabled(),
                properties.getMetrics().getExport().getOtlp().getEndpoint());
        log.info("Resource Attributes: {}", properties.getResourceAttributes());
        log.info("Common Tags: {}", properties.getCommonTags());
        if (properties.getTraces().isEnabled()) {
            log.info("Tracing: enabled=true, exposeHeaders={}, samplingRate={}",
                    properties.getTraces().isExposeHeaders(),
                    properties.getTraces().getSampling().getRate());
        }
    }

    /**
     * Creates a {@link MeterRegistryCustomizer} that applies common tags and resource attributes
     * to all metrics registered in the {@link MeterRegistry}.
     * <p>
     * Common tags and resource attributes are merged, with resource attributes taking precedence
     * for duplicate keys.
     * </p>
     */
    @Bean
    @ConditionalOnMissingBean(name = "chassisObservabilityMeterRegistryCustomizer")
    @ConditionalOnProperty(
            prefix = "chassis.observability.metrics",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public MeterRegistryCustomizer<MeterRegistry> chassisObservabilityMeterRegistryCustomizer() {
        log.info("Initializing chassisObservabilityMeterRegistryCustomizer...");

        return registry -> {
            var allTags = properties.getCommonTags().entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey(),
                            e -> e.getValue()
                    ));
            allTags.putAll(properties.getResourceAttributes());

            var convertedTags = convertToMicrometerTags(allTags);
            registry.config().commonTags(convertedTags);

            log.debug("Applied {} common tags to MeterRegistry: {}",
                    convertedTags.size(),
                    convertedTags.stream().map(Tag::getKey).collect(Collectors.joining(", ")));
        };
    }

    private List<Tag> convertToMicrometerTags(Map<String, String> allTags) {
        return allTags.entrySet().stream()
                .filter(e -> StringUtils.hasText(e.getValue())) // Filter out null/empty values
                .map(e -> Tag.of(e.getKey(), e.getValue()))
                .toList();

    }

    @Bean
    @ConditionalOnClass(Span.class)
    @ConditionalOnMissingBean(ChassisTraceResponseHeaderFilter.class)
    @ConditionalOnProperty(
            prefix = "chassis.observability.traces",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    @ConditionalOnProperty(
            prefix = "chassis.observability.traces",
            name = "exposeHeaders",
            havingValue = "true",
            matchIfMissing = true
    )
    public ChassisTraceResponseHeaderFilter chassisTraceResponseHeaderFilter() {
        log.info("Initializing chassisTraceResponseHeaderFilter...");
        return new ChassisTraceResponseHeaderFilter();
    }

    /**
     * Creates a customizer for Spring Boot's management OTLP configuration.
     * <p>
     * This bean bridges chassis.observability properties to Spring Boot's native
     * management.otlp.metrics.export configuration. This ensures compatibility with
     * Spring Boot's built-in OTLP support while allowing users to configure via
     * the chassis.observability namespace.
     * </p>
     * <p>
     * Properties mapped:
     * <ul>
     *   <li>chassis.observability.metrics.export.otlp.endpoint → management.otlp.metrics.export.url</li>
     *   <li>chassis.observability.metrics.export.otlp.step → management.otlp.metrics.export.step</li>
     *   <li>chassis.observability.authentication → management.otlp.metrics.export.headers.authorization</li>
     *   <li>chassis.observability.resource-attributes → management.otlp.metrics.export.resource-attributes</li>
     * </ul>
     * </p>
     * <p>
     * Note: This bean is registered but the actual property bridging happens through
     * Spring Boot's property resolution mechanism. The properties are documented here
     * for clarity, but users can also configure management.otlp directly if preferred.
     * </p>
     *
     * @return Customizer bean (marker for documentation purposes)
     */
    @Bean
    @ConditionalOnMissingBean(name = "chassisObservabilityPropertiesBridge")
    @ConditionalOnProperty(
            prefix = "chassis.observability.metrics.export.otlp",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public ChassisObservabilityPropertiesBridge chassisObservabilityPropertiesBridge() {
        log.info("Bridging chassis.observability properties to management.otlp configuration");
        return new ChassisObservabilityPropertiesBridge(properties);
    }

    /**
     * Marker class for property bridging documentation.
     * <p>
     * Spring Boot's property resolution automatically handles the bridging between
     * chassis.observability and management.otlp properties. This bean serves as
     * documentation and can be extended in the future to provide programmatic
     * property manipulation if needed.
     * </p>
     */
    @RequiredArgsConstructor
    public static class ChassisObservabilityPropertiesBridge {
        private final ChassisObservabilityProperties properties;

        /**
         * Returns the observability properties being bridged.
         *
         * @return ChassisObservabilityProperties instance
         */
        public ChassisObservabilityProperties getProperties() {
            return properties;
        }
    }
}
