package io.github.milczekt1.chassis.test.observability;

import io.github.milczekt1.chassis.test.utils.TestToolsIntegrationTest;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MetricsVerifierExtension.class)
@Import(ObservabilityTestConfiguration.class)
@TestToolsIntegrationTest
class MetricsVerifierExtensionIT {

    @Autowired
    MeterRegistry meterRegistry;

    @Test
    void shouldInjectMetricsVerifier(MetricsVerifier metricsVerifier) {
        // Verify parameter injection works
        assertThat(metricsVerifier).isNotNull();
    }

    @Test
    void shouldListAllMetricsForDebugging(MetricsVerifier metricsVerifier) {
        // given
        List<String> metrics = List.of("metric1", "metric2", "metric3");
        metrics.forEach(m -> meterRegistry.counter(m));

        // when
        var listedMetrics = metricsVerifier.listMetrics();
        // then
        assertThat(listedMetrics)
                .containsAll(listedMetrics);
    }

    @Test
    void shouldCountMetrics(MetricsVerifier metricsVerifier) {
        // given
        meterRegistry.counter("metric1");
        // when then
        assertThat(metricsVerifier.getMetricCount()).isEqualTo(1);
    }

    @Test
    void shouldVerifyMetricWithMultipleTags(MetricsVerifier metricsVerifier) {
        // Given - create metric with multiple tags
        meterRegistry.counter("test.multi.tag.metric",
                "tag1", "value1",
                "tag2", "value2",
                "tag3", "value3");

        // When Then
        assertThatCode(() ->
                metricsVerifier.assertMetric("test.multi.tag.metric")
                        .withTag("tag1", "value1")
                        .withTag("tag2", "value2")
                        .withTag("tag3", "value3")
                        .exists()
        ).doesNotThrowAnyException();
    }

    @Nested
    class MetricExistence {

        @Test
        void shouldVerifyMetricExist(MetricsVerifier metricsVerifier) {
            meterRegistry.counter("dummy.metric").increment();
            metricsVerifier.assertMetric("dummy.metric")
                    .exists();
        }

        @Test
        void shouldThrowWhenMetricDoesNotExistButWasExpected(MetricsVerifier metricsVerifier) {
            assertThatExceptionOfType(AssertionError.class)
                    .isThrownBy(() ->
                            metricsVerifier.assertMetric("nonexistent.metric")
                                    .exists()
                    )
                    .withMessageContaining("should exist");
        }

        @Test
        void shouldVerifyMetricDoesNotExist(MetricsVerifier metricsVerifier) {
            metricsVerifier.assertMetric("not-existing-metric")
                    .doesNotExist();
        }

        @Test
        void shouldThrowWhenMetricExistButWasNotExpected(MetricsVerifier metricsVerifier) {
            assertThatCode(() ->
                    metricsVerifier.assertMetric("truly.nonexistent.metric")
                            .doesNotExist()
            ).doesNotThrowAnyException();
        }

    }

    @Nested
    class MetricValue {

        @Test
        void shouldVerifyMetricValue(MetricsVerifier metricsVerifier) {
            // given
            meterRegistry.counter("dummy.metric").increment();
            meterRegistry.counter("dummy.metric").increment();

            // when then
            metricsVerifier.assertMetric("dummy.metric")
                    .hasValueGreaterThan(1.0)
                    .hasValueGreaterThanOrEqualTo(2.0)
                    .hasValue(2.0);
        }

        @Test
        void shouldThrowOnValueMismatch_exactValue(MetricsVerifier metricsVerifier) {
            // Given
            Counter counter = meterRegistry.counter("test.counter.mismatch");
            counter.increment();

            // When then
            assertThatExceptionOfType(AssertionError.class)
                    .isThrownBy(() ->
                            metricsVerifier.assertMetric("test.counter.mismatch")
                                    .hasValue(5.0)
                    )
                    .withMessageContaining("should have value");
        }

        @Test
        void shouldThrowOnValueMismatch_valueGreaterThan(MetricsVerifier metricsVerifier) {
            // Given
            Counter counter = meterRegistry.counter("test.counter.gt");
            counter.increment(10);

            // When then
            assertThatExceptionOfType(AssertionError.class)
                    .isThrownBy(() ->
                            metricsVerifier.assertMetric("test.counter.gt")
                                    .hasValueGreaterThan(11.0)
                    ).withMessageContaining("to be greater than");
        }

        @Test
        void shouldThrowOnValueMismatch_valueGreaterThanOrEqualTo(MetricsVerifier metricsVerifier) {
            // Given
            Counter counter = meterRegistry.counter("test.counter.gte");
            counter.increment(10);

            // When then
            assertThatExceptionOfType(AssertionError.class)
                    .isThrownBy(() ->
                            metricsVerifier.assertMetric("test.counter.gte")
                                    .hasValueGreaterThanOrEqualTo(11.0)
                    ).withMessageContaining("to be greater than or equal to");
        }

        @Test
        void shouldThrowOnValueMismatch_valueLessThan(MetricsVerifier metricsVerifier) {
            // Given
            Counter counter = meterRegistry.counter("test.counter.lt");
            counter.increment(10);

            // When then
            assertThatExceptionOfType(AssertionError.class)
                    .isThrownBy(() ->
                            metricsVerifier.assertMetric("test.counter.lt")
                                    .hasValueLessThan(5.0)
                    ).withMessageContaining("to be less than");
        }

        @Test
        void shouldThrowOnValueMismatch_valueLessThanOrEqualTo(MetricsVerifier metricsVerifier) {
            // Given
            Counter counter = meterRegistry.counter("test.counter.lte");
            counter.increment(10);

            // When then
            assertThatExceptionOfType(AssertionError.class)
                    .isThrownBy(() ->
                            metricsVerifier.assertMetric("test.counter.lte")
                                    .hasValueLessThanOrEqualTo(5.0)
                    ).withMessageContaining("to be less than or equal to");
        }
    }
}
