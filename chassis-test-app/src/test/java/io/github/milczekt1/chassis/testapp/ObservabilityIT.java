package io.github.milczekt1.chassis.testapp;

import io.github.milczekt1.chassis.test.observability.MetricsVerifier;
import io.github.milczekt1.chassis.test.observability.TraceVerifier;
import io.github.milczekt1.chassis.testapp.utils.TestAppIntegrationTest;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;

@TestAppIntegrationTest
class ObservabilityIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MeterRegistry meterRegistry;


    @Nested
    class Metrics {

        @Test
        void shouldAttachTagsAutomatically(MetricsVerifier metricsVerifier) {
            // given
            final var metricBefore = meterRegistry.counter("counter.hit").count();

            // when
            restTemplate.getForEntity("/metrics/counter", String.class);

            // then
            metricsVerifier.assertMetric("counter.hit")
                    // resource attributes
                    .withTag("service.name", "chassis-test-app")
                    .withTag("service.namespace", "chassis-test-app")
                    .withTag("deployment.environment", "local")
                    // common tags
                    .withTag("chassis", "true")
                    .exists()
                    .hasValue(metricBefore + 1.0);
        }

        @Test
        void shouldVerifyMetricValue(MetricsVerifier metricsVerifier) {
            // given
            final var metricBefore = meterRegistry.counter("counter.hit").count();
            restTemplate.getForEntity("/metrics/counter", String.class);
            restTemplate.getForEntity("/metrics/counter", String.class);

            metricsVerifier.assertMetric("counter.hit")
                    .withTag("service.name", "chassis-test-app")
                    .hasValueGreaterThan(metricBefore + 1.0)
                    .hasValueGreaterThanOrEqualTo(metricBefore + 2.0)
                    .hasValue(metricBefore + 2.0);
        }
    }

    @Test
    @Disabled("Requires OpenTelemetry Java agent to run." +
            " mvn test -Dspring-boot.run.jvmArguments=\"-javaagent:agent/grafana-opentelemetry-java.jar")
    void shouldVerifyTracingResponseHeaders(TraceVerifier traceVerifier) {
        // when
        final var response = restTemplate.getForEntity("/metrics/counter", String.class);
        // then
        traceVerifier.assertResponseHasValidTraceHeaders(response);
        // and
        traceVerifier.assertTraceIdValid(response.getHeaders().getFirst("Trace-Id"));
        traceVerifier.assertSpanIdValid(response.getHeaders().getFirst("Span-Id"));
    }
}
