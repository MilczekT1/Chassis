package io.github.milczekt1.chassis.observability.tracing;

import io.github.milczekt1.chassis.observability.tracing.utils.TraceFilterIntegrationTest;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TraceFilterIntegrationTest
class ChassisTraceResponseHeaderFilterIT {

    private static final String TRACE_ID = "0af7651916cd43dd8448eb211c80319c";
    private static final String SPAN_ID = "b7ad6b7169203331";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAddTraceAndSpanHeadersWhenValidSpanIsActive() throws Exception {
        final SpanContext spanContext = SpanContext.create(
                TRACE_ID, SPAN_ID, TraceFlags.getSampled(), TraceState.getDefault());

        try (final var ignored = Span.wrap(spanContext).makeCurrent()) {
            mockMvc.perform(get("/test/tracing/ping"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Trace-Id", TRACE_ID))
                    .andExpect(header().string("Span-Id", SPAN_ID));
        }
    }

    @Test
    void shouldNotAddHeadersWhenNoActiveSpan() throws Exception {
        mockMvc.perform(get("/test/tracing/ping"))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("Trace-Id"))
                .andExpect(header().doesNotExist("Span-Id"));
    }
}
