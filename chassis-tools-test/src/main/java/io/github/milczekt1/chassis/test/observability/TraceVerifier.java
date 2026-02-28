package io.github.milczekt1.chassis.test.observability;

import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Utility for verifying distributed tracing data in tests.
 * <p>
 * Provides assertions for trace IDs, span IDs, trace propagation,
 * and response headers. This verifier works with trace data (IDs, headers)
 * and does not require an active OpenTelemetry agent.
 * </p>
 * <p>
 * Use with {@link TraceVerifierExtension} to inject into test methods.
 * </p>
 *
 * <h3>Example Usage:</h3>
 * <pre>
 * {@code
 * @ExtendWith(TraceVerifierExtension.class)
 * class TracingIT {
 *
 *     @Test
 *     void testResponseHeaders(TraceVerifier traceVerifier, TestRestTemplate restTemplate) {
 *         ResponseEntity<String> response = restTemplate.getForEntity("/api/test", String.class);
 *         traceVerifier.assertResponseHasTraceHeaders(response);
 *     }
 *
 *     @Test
 *     void testTracePropagation(TraceVerifier traceVerifier) {
 *         String traceparent = "00-aaaabbbbccccdddd1111222233334444-0011223344556677-01";
 *         String traceId = traceVerifier.extractTraceIdFromTraceparent(traceparent);
 *         traceVerifier.assertTraceIdValid(traceId);
 *     }
 * }
 * }
 * </pre>
 *
 * <h3>Note on Active Span Context:</h3>
 * <p>
 * This verifier does not provide methods to access the current active span
 * (e.g., {@code Span.current()}) as those require the OpenTelemetry Java agent
 * to be running. For integration tests with active tracing, access the span
 * directly in your test:
 * </p>
 * <pre>
 * {@code
 * import io.opentelemetry.api.trace.Span;
 *
 * @Test
 * void testActiveTrace(TraceVerifier traceVerifier) {
 *     // Get current trace ID from active span
 *     String traceId = Span.current().getSpanContext().getTraceId();
 *     traceVerifier.assertTraceIdValid(traceId);
 * }
 * }
 * </pre>
 */
public class TraceVerifier {

    public TraceVerifier assertTraceIdValid(String traceId) {
        assertThat(traceId)
                .as("Trace ID should be 32 hex characters")
                .matches("^[0-9a-f]{32}$");
        return this;
    }

    public TraceVerifier assertSpanIdValid(String spanId) {
        assertThat(spanId)
                .as("Span ID should be 16 hex characters")
                .matches("^[0-9a-f]{16}$");
        return this;
    }

    /**
     * Extract trace ID from W3C traceparent header.
     * <p>
     * Format: {@code 00-<trace-id>-<span-id>-<flags>}
     * </p>
     *
     * @param traceparent the traceparent header value
     * @return extracted trace ID
     */
    public String extractTraceIdFromTraceparent(String traceparent) {
        assertThat(traceparent)
                .as("traceparent should match W3C format")
                .matches("^00-[0-9a-f]{32}-[0-9a-f]{16}-[0-9a-f]{2}$");
        return traceparent.substring(3, 35);
    }

    /**
     * Extract span ID from W3C traceparent header.
     * <p>
     * Format: {@code 00-<trace-id>-<span-id>-<flags>}
     * </p>
     *
     * @param traceparent the traceparent header value
     * @return extracted span ID
     */
    public String extractSpanIdFromTraceparent(String traceparent) {
        assertThat(traceparent)
                .as("traceparent should match W3C format")
                .matches("^00-[0-9a-f]{32}-[0-9a-f]{16}-[0-9a-f]{2}$");
        return traceparent.substring(36, 52);
    }

    public TraceVerifier assertResponseHasValidTraceHeaders(ResponseEntity<?> response) {
        assertThat(response.getHeaders())
                .as("Response should contain Trace-Id header")
                .matches(headers -> headers.getFirst("Trace-Id") != null);
        assertThat(response.getHeaders())
                .as("Response should contain Span-Id header")
                .matches(headers -> headers.getFirst("Span-Id") != null);

        String traceId = response.getHeaders().getFirst("Trace-Id");
        String spanId = response.getHeaders().getFirst("Span-Id");

        assertTraceIdValid(traceId);
        assertSpanIdValid(spanId);

        return this;
    }
}
