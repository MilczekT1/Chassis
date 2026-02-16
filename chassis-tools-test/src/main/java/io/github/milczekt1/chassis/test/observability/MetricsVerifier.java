package io.github.milczekt1.chassis.test.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Utility for verifying metrics in tests.
 * <p>
 * Provides fluent API for asserting on metrics, tags, and values.
 * Use with {@link MetricsVerifierExtension} to inject into test methods.
 * </p>
 *
 * <h3>Example Usage:</h3>
 * <pre>
 * {@code
 * @ExtendWith(MetricsVerifierExtension.class)
 * class MyTest {
 *     @Test
 *     void testMetrics(MetricsVerifier metricsVerifier) {
 *         metricsVerifier.assertMetric("http.server.requests")
 *             .hasTag("method", "GET")
 *             .hasTag("status", "200")
 *             .exists();
 *
 *         metricsVerifier.assertCommonTag("application", "my-service");
 *     }
 * }
 * }
 * </pre>
 */
@RequiredArgsConstructor
public class MetricsVerifier {

    private static final String METRIC_SHOULD_EXIST = "Metric '%s' should exist as counter";

    private final MeterRegistry meterRegistry;

    public MetricAssertion assertMetric(String metricName) {
        return new MetricAssertion(meterRegistry, metricName);
    }

    public List<String> listMetrics() {
        return meterRegistry.getMeters().stream()
                .map(meter -> meter.getId().getName())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public int getMetricCount() {
        return meterRegistry.getMeters().size();
    }

    public static class MetricAssertion {
        private final MeterRegistry registry;
        private final String metricName;
        private final List<Tag> expectedTags = new ArrayList<>();

        MetricAssertion(MeterRegistry registry, String metricName) {
            this.registry = registry;
            this.metricName = metricName;
        }

        public MetricAssertion withTag(String key, String value) {
            expectedTags.add(Tag.of(key, value));
            return this;
        }

        public MetricAssertion exists() {
            Meter meter = registry.find(metricName).tags(expectedTags).meter();
            assertThat(meter)
                    .as("Metric '%s' with tags %s should exist", metricName, expectedTags)
                    .isNotNull();
            return this;
        }

        public MetricAssertion doesNotExist() {
            Meter meter = registry.find(metricName).tags(expectedTags).meter();
            assertThat(meter)
                    .as("Metric '%s' with tags %s should not exist", metricName, expectedTags)
                    .isNull();
            return this;
        }

        public MetricAssertion hasValue(double expected) {
            Counter counter = registry.find(metricName).tags(expectedTags).counter();
            assertThat(counter)
                    .as(METRIC_SHOULD_EXIST, metricName)
                    .isNotNull();
            assertThat(counter.count())
                    .as("Counter '%s' should have value %s", metricName, expected)
                    .isEqualTo(expected);
            return this;
        }

        public MetricAssertion hasValueGreaterThan(double expected) {
            Counter counter = registry.find(metricName).tags(expectedTags).counter();
            assertThat(counter)
                    .as(METRIC_SHOULD_EXIST, metricName)
                    .isNotNull();
            assertThat(counter.count())
                    .as("Counter '%s' should have value greater than %s", metricName, expected)
                    .isGreaterThan(expected);
            return this;
        }

        public MetricAssertion hasValueGreaterThanOrEqualTo(double expected) {
            Counter counter = registry.find(metricName).tags(expectedTags).counter();
            assertThat(counter)
                    .as(METRIC_SHOULD_EXIST, metricName)
                    .isNotNull();
            assertThat(counter.count())
                    .as("Counter '%s' should have value >= %s", metricName, expected)
                    .isGreaterThanOrEqualTo(expected);
            return this;
        }

        public MetricAssertion hasValueLessThan(double expected) {
            Counter counter = registry.find(metricName).tags(expectedTags).counter();
            assertThat(counter)
                    .as(METRIC_SHOULD_EXIST, metricName)
                    .isNotNull();
            assertThat(counter.count())
                    .as("Counter '%s' should have value greater than %s", metricName, expected)
                    .isLessThan(expected);
            return this;
        }

        public MetricAssertion hasValueLessThanOrEqualTo(double expected) {
            Counter counter = registry.find(metricName).tags(expectedTags).counter();
            assertThat(counter)
                    .as(METRIC_SHOULD_EXIST, metricName)
                    .isNotNull();
            assertThat(counter.count())
                    .as("Counter '%s' should have value >= %s", metricName, expected)
                    .isLessThanOrEqualTo(expected);
            return this;
        }
    }
}
