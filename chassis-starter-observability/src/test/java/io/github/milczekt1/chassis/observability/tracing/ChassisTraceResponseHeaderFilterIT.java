package io.github.milczekt1.chassis.observability.tracing;

import io.github.milczekt1.chassis.observability.tracing.utils.TraceFilterIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static io.github.milczekt1.chassis.observability.tracing.utils.TestSpan.SPAN_ID;
import static io.github.milczekt1.chassis.observability.tracing.utils.TestSpan.TRACE_ID;
import static io.github.milczekt1.chassis.observability.tracing.utils.TestSpan.inActiveSpan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TraceFilterIntegrationTest
class ChassisTraceResponseHeaderFilterIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAddTraceAndSpanHeadersWhenValidSpanIsActive() throws Exception {
        inActiveSpan(() -> mockMvc.perform(get("/test/tracing/ping"))
                .andExpect(status().isOk())
                .andExpect(header().string("Trace-Id", TRACE_ID))
                .andExpect(header().string("Span-Id", SPAN_ID)));
    }

    @Test
    void shouldNotAddHeadersWhenNoActiveSpan() throws Exception {
        mockMvc.perform(get("/test/tracing/ping"))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("Trace-Id"))
                .andExpect(header().doesNotExist("Span-Id"));
    }
}
