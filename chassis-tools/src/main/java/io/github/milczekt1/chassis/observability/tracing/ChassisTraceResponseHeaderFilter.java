package io.github.milczekt1.chassis.observability.tracing;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * HTTP filter that adds OpenTelemetry trace context to response headers.
 * <p>
 * This filter adds the following headers to every HTTP response:
 * <ul>
 *   <li><b>Trace-Id</b>: The trace ID of the current span (128-bit hex string)</li>
 *   <li><b>Span-Id</b>: The span ID of the current span (64-bit hex string)</li>
 * </ul>
 * </p>
 * <p>
 * These headers enable:
 * <ul>
 *   <li>Client-side correlation of requests with backend traces</li>
 *   <li>Debugging by providing trace IDs in logs or error messages</li>
 *   <li>End-to-end trace propagation across service boundaries</li>
 * </ul>
 * </p>
 * <p>
 * The filter only adds headers when a valid trace context exists.
 * If no trace is active, headers are not added.
 * </p>
 */
@Slf4j
public class ChassisTraceResponseHeaderFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_HEADER = "Trace-Id";
    private static final String SPAN_ID_HEADER = "Span-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            final var spanContext = getSpanContext();
            if (spanContext.isValid()) {
                String traceId = spanContext.getTraceId();
                String spanId = spanContext.getSpanId();

                response.setHeader(TRACE_ID_HEADER, traceId);
                response.setHeader(SPAN_ID_HEADER, spanId);

                log.trace("Added trace headers to response: Trace-Id={}, Span-Id={}", traceId, spanId);
            }
        } catch (Exception e) {
            log.warn("Failed to add trace headers to response", e);
        }
        filterChain.doFilter(request, response);
    }

    private SpanContext getSpanContext() {
        Span currentSpan = Span.current();
        return currentSpan.getSpanContext();
    }
}
