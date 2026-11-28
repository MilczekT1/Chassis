package io.github.milczekt1.chassis.observability.tracing;

import io.github.milczekt1.chassis.observability.tracing.utils.TraceFilterIntegrationTest;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.context.Scope;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static io.github.milczekt1.chassis.observability.tracing.utils.TraceTestController.PRESET_INSTANCE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@TraceFilterIntegrationTest
class ChassisTraceInstanceInterceptorIT {

    private static final String TRACE_ID = "0af7651916cd43dd8448eb211c80319c";
    private static final String SPAN_ID = "b7ad6b7169203331";

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(strings = {"/test/tracing/problem", "/test/tracing/problem-entity"})
    void setsInstanceToTraceUrnWhenSpanIsActive(String path) throws Exception {
        try (Scope _ = activeSpan()) {
            mockMvc.perform(get(path))
                    .andExpect(jsonPath("$.instance").value("urn:trace:" + TRACE_ID));
        }
    }

    @Test
    void leavesInstanceUnsetWithoutActiveSpan() throws Exception {
        mockMvc.perform(get("/test/tracing/problem"))
                .andExpect(jsonPath("$.instance").doesNotExist());
    }

    @Test
    void keepsInstanceTheHandlerSet() throws Exception {
        try (Scope _ = activeSpan()) {
            mockMvc.perform(get("/test/tracing/problem-with-instance"))
                    .andExpect(jsonPath("$.instance").value(PRESET_INSTANCE));
        }
    }

    private static Scope activeSpan() {
        return Span.wrap(SpanContext.create(TRACE_ID, SPAN_ID, TraceFlags.getSampled(), TraceState.getDefault()))
                .makeCurrent();
    }
}
