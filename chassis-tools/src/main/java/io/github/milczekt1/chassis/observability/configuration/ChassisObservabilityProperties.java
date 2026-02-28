package io.github.milczekt1.chassis.observability.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for Chassis Observability auto-configuration.
 * <p>
 * Configures OTLP-based observability for metrics, traces, and logs export to backends like Grafana Cloud.
 * </p>
 * <p>
 * Default configuration provides sensible defaults for development with localhost OTLP endpoint.
 * Production deployments should override endpoints and authentication via environment variables.
 * </p>
 * <p>
 * Example configuration:
 * <pre>
 * chassis:
 *   observability:
 *     enabled: true
 *     metrics:
 *       export:
 *         otlp:
 *           endpoint: ${OTLP_ENDPOINT_URL:http://localhost:4318/v1/metrics}
 *           step: 10s
 *     authentication:
 *       basic:
 *         username: ${OTLP_USERNAME:}
 *         password: ${OTLP_PASSWORD:}
 *     resource-attributes:
 *       service.name: ${spring.application.name}
 *       service.namespace: ${SERVICE_NAMESPACE:default}
 *       deployment.environment: ${DEPLOYMENT_ENVIRONMENT:development}
 * </pre>
 *
 * @see ChassisObservabilityAutoConfiguration
 */
@Data
@ConfigurationProperties(prefix = "chassis.observability")
public class ChassisObservabilityProperties {

    private static final String DEFAULT_PROTOCOL = "http/protobuf";

    private boolean enabled = true;
    private Metrics metrics = new Metrics();
    private Traces traces = new Traces();
    private Logs logs = new Logs();

    /**
     * Authentication configuration for OTLP endpoints.
     */
    private Authentication authentication = new Authentication();

    /**
     * Resource attributes to attach to all telemetry signals.
     * These attributes identify the service and its environment.
     */
    private Map<String, String> resourceAttributes = new HashMap<>();

    /**
     * Common tags to attach to all metrics.
     * These tags are in addition to resource attributes.
     */
    private Map<String, String> commonTags = new HashMap<>();

    @Data
    public static class Metrics {

        private boolean enabled = true;

        private Export export = new Export();

        @Data
        public static class Export {

            private Otlp otlp = new Otlp();

            @Data
            public static class Otlp {

                private boolean enabled = true;

                /**
                 * OTLP endpoint URL for metrics export.
                 * Should include the full path to the metrics endpoint.
                 * <p>
                 * Default: http://localhost:4318/v1/metrics (for local development)
                 * <p>
                 * Examples:
                 * - Localhost: http://localhost:4318/v1/metrics
                 * - Grafana Cloud: https://otlp-gateway-{region}.grafana.net/otlp/v1/metrics
                 * </p>
                 */
                private String endpoint = "http://localhost:4318/v1/metrics";

                /**
                 * OTLP protocol to use.
                 * Supported values: http/protobuf, grpc
                 */
                private String protocol = DEFAULT_PROTOCOL;

                /**
                 * Export interval - how frequently metrics are pushed to the OTLP endpoint.
                 * <p>
                 * Default: 60s (recommended for production to avoid rate limits)
                 * <p>
                 * Shorter intervals (10s-30s) are useful for development/testing but increase
                 * load on the OTLP receiver and may hit rate limits with cloud providers.
                 * </p>
                 */
                private Duration step = Duration.ofSeconds(60);
            }
        }
    }

    @Data
    public static class Traces {

        private boolean enabled = true;
        private boolean exposeHeaders = true;
        private Sampling sampling = new Sampling();
        private Export export = new Export();

        @Data
        public static class Export {

            private Otlp otlp = new Otlp();

            @Data
            public static class Otlp {

                private boolean enabled = true;

                /**
                 * OTLP endpoint URL for traces export.
                 * Should include the full path to the traces endpoint.
                 * <p>
                 * Examples:
                 * - Localhost: http://localhost:4318/v1/traces
                 * - Grafana Cloud: https://otlp-gateway-{region}.grafana.net/otlp/v1/traces
                 * </p>
                 */
                private String endpoint = "http://localhost:4318/v1/traces";

                /**
                 * OTLP protocol to use.
                 * Supported values: http/protobuf, grpc
                 */
                private String protocol = DEFAULT_PROTOCOL;
            }
        }

        @Data
        public static class Sampling {
            /**
             * Sampling rate as a decimal between 0.0 and 1.0.
             * - 0.0 = no traces sampled (0%)
             * - 1.0 = all traces sampled (100%)
             * - 0.1 = 10% of traces sampled
             */
            private double rate = 1.0;

            /**
             * Sampling strategy.
             * - PARENT_BASED: Use parent trace's sampling decision if available, otherwise use rate
             * - ALWAYS_ON: Always sample (ignores rate)
             * - ALWAYS_OFF: Never sample (ignores rate)
             * - TRACE_ID_RATIO: Sample based on rate
             */
            private Strategy strategy = Strategy.PARENT_BASED;

            public enum Strategy {
                /**
                 * Respect parent trace's sampling decision, fall back to rate-based sampling.
                 * This is the recommended setting for distributed tracing.
                 */
                PARENT_BASED,
                ALWAYS_ON,
                ALWAYS_OFF,
                TRACE_ID_RATIO
            }
        }
    }

    @Data
    public static class Logs {

        private boolean enabled = true;
        private Export export = new Export();

        @Data
        public static class Export {

            private Otlp otlp = new Otlp();

            @Data
            public static class Otlp {

                private boolean enabled = true;

                /**
                 * OTLP endpoint URL for logs export.
                 * Should include the full path to the logs endpoint.
                 * <p>
                 * Examples:
                 * - Localhost: http://localhost:4318/v1/logs
                 * - Grafana Cloud: https://otlp-gateway-{region}.grafana.net/otlp/v1/logs
                 * </p>
                 */
                private String endpoint = "http://localhost:4318/v1/logs";

                /**
                 * OTLP protocol to use.
                 * Supported values: http/protobuf, grpc
                 */
                private String protocol = DEFAULT_PROTOCOL;
            }
        }
    }

    @Data
    public static class Authentication {
        /**
         * Authentication type.
         * Supported values: basic, header, none
         */
        private Type type = Type.BASIC;

        /**
         * Basic authentication configuration.
         * Used when type=basic.
         */
        private Basic basic = new Basic();

        /**
         * Custom header authentication configuration.
         * Used when type=header.
         */
        private Header header = new Header();

        public enum Type {

            NONE,

            /**
             * Basic authentication with username and password.
             * Credentials are sent as Base64-encoded "username:password" in Authorization header.
             */
            BASIC,

            /**
             * Custom header authentication.
             * Allows specifying custom header name and value.
             */
            HEADER
        }

        @Data
        public static class Basic {
            private String username;
            private String password;
        }

        @Data
        public static class Header {
            /**
             * Custom header name for authentication.
             * Example: Authorization, X-API-Key
             */
            private String name = "Authorization";

            /**
             * Custom header value for authentication.
             * Example: Bearer token123, Basic base64encodedcreds
             */
            private String value;
        }
    }
}
