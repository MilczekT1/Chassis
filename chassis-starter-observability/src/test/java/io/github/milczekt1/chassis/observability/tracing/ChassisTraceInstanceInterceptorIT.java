package io.github.milczekt1.chassis.observability.tracing;

import io.github.milczekt1.chassis.observability.tracing.utils.TraceFilterIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static io.github.milczekt1.chassis.observability.tracing.utils.TestSpan.TRACE_ID;
import static io.github.milczekt1.chassis.observability.tracing.utils.TestSpan.inActiveSpan;
import static io.github.milczekt1.chassis.observability.tracing.utils.TraceTestController.PRESET_INSTANCE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@TraceFilterIntegrationTest
class ChassisTraceInstanceInterceptorIT {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(strings = {"/test/tracing/problem", "/test/tracing/problem-entity"})
    void setsInstanceToTraceUrnWhenSpanIsActive(String path) throws Exception {
        inActiveSpan(() -> mockMvc.perform(get(path))
                .andExpect(jsonPath("$.instance").value("urn:trace:" + TRACE_ID)));
    }

    @Test
    void leavesInstanceUnsetWithoutActiveSpan() throws Exception {
        mockMvc.perform(get("/test/tracing/problem"))
                .andExpect(jsonPath("$.instance").doesNotExist());
    }

    @Test
    void keepsInstanceTheHandlerSet() throws Exception {
        inActiveSpan(() -> mockMvc.perform(get("/test/tracing/problem-with-instance"))
                .andExpect(jsonPath("$.instance").value(PRESET_INSTANCE)));
    }
}
