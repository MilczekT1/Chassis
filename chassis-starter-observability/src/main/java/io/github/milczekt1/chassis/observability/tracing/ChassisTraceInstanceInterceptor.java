package io.github.milczekt1.chassis.observability.tracing;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

import java.net.URI;

/**
 * Sets a problem detail's {@code instance} to {@code urn:trace:<trace ID>} when a span is current and no instance is
 * set, so the error occurrence can be looked up in the traces.
 */
public class ChassisTraceInstanceInterceptor implements ErrorResponse.Interceptor {

    private static final String TRACE_URN_PREFIX = "urn:trace:";

    @Override
    public void handleError(ProblemDetail detail, @Nullable ErrorResponse errorResponse) {
        SpanContext spanContext = Span.current().getSpanContext();
        if (detail.getInstance() == null && spanContext.isValid()) {
            detail.setInstance(URI.create(TRACE_URN_PREFIX + spanContext.getTraceId()));
        }
    }
}
