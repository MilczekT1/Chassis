package io.github.milczekt1.chassis.observability.tracing.utils;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.context.Context;

import java.util.concurrent.Callable;

public final class TestSpan {

    public static final String TRACE_ID = "0af7651916cd43dd8448eb211c80319c";
    public static final String SPAN_ID = "b7ad6b7169203331";

    private TestSpan() {
    }

    /**
     * Runs {@code request} with a span of {@link #TRACE_ID} and {@link #SPAN_ID} current.
     */
    public static void inActiveSpan(Callable<?> request) throws Exception {
        Span span = Span.wrap(SpanContext.create(TRACE_ID, SPAN_ID, TraceFlags.getSampled(), TraceState.getDefault()));
        Context.current().with(span).wrap(request).call();
    }
}
