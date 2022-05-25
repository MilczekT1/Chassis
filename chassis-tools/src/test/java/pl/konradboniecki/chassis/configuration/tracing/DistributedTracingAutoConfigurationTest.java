package pl.konradboniecki.chassis.configuration.tracing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.konradboniecki.chassis.testutils.ChassisTestApplication;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = { ChassisTestApplication.class, DistributedTracingAutoConfiguration.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "budget.chassis.tracing.traceIdKey=X-overridden-trace-id-key",
                "budget.chassis.tracing.spanIdKey=X-overridden-span-id-key",
                "spring.config.import=optional:configserver:"
        }
)
class DistributedTracingAutoConfigurationTest {

    @Autowired(required = false)
    private HttpResponseTraceIdInjector httpResponseTraceIdInjector;
    @Autowired(required = false)
    private HttpResponseTraceIdInjectorFilter httpResponseTraceIdInjectorFilter;

    @Autowired
    private DistributedTracingProperties tracingProperties;

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate testRestTemplate;


    @Test
    void givenAutoConfiguration_thenBeansAreInitialized() {
        // Then:
        assertThat(httpResponseTraceIdInjector).isNotNull();
        assertThat(httpResponseTraceIdInjectorFilter).isNotNull();
        assertThat(tracingProperties).isNotNull();
    }

    @Test
    void whenDistributedTracingHeaderKeysAreOverridden_thenOverriddenValuesAreUsed() {
        // When:
        final var traceIdKey = tracingProperties.getTraceIdKey();
        final var spanIdKey = tracingProperties.getSpanIdKey();
        // Then:
        assertThat(traceIdKey)
                .isNotNull()
                .isEqualTo("X-overridden-trace-id-key");
        assertThat(spanIdKey)
                .isNotNull()
                .isEqualTo("X-overridden-span-id-key");
    }

    @Test
    void whenRequestIsSent_thenTraceIdHeaderIsPresent() {
        // Given:
        final var url = "http://localhost:" + port;
        // When:
        final var response = testRestTemplate.getForEntity(url, String.class);
        // Then:
        assertThat(response.getHeaders().get("X-overridden-trace-id-key").get(0))
                .isNotNull()
                .isInstanceOf(String.class);
    }

    @Test
    void whenRequestIsSent_thenSpanIdHeaderIsPresent() {
        // Given:
        final var url = "http://localhost:" + port;
        // When:
        final var response = testRestTemplate.getForEntity(url, String.class);
        // Then:
        assertThat(response.getHeaders().get("X-overridden-span-id-key").get(0))
                .isNotNull()
                .isInstanceOf(String.class);
    }
}
