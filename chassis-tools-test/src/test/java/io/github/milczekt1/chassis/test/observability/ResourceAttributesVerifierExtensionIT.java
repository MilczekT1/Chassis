package io.github.milczekt1.chassis.test.observability;

import io.github.milczekt1.chassis.test.utils.TestToolsIntegrationTest;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static io.github.milczekt1.chassis.test.observability.ObservabilityTestConfiguration.TAG_APPLICATION_KEY;
import static io.github.milczekt1.chassis.test.observability.ObservabilityTestConfiguration.TAG_APPLICATION_VALUE;
import static org.assertj.core.api.Assertions.*;

@TestToolsIntegrationTest
@ExtendWith(ResourceAttributesVerifierExtension.class)
@Import(ObservabilityTestConfiguration.class)
class ResourceAttributesVerifierExtensionIT {

    @Autowired
    private MeterRegistry meterRegistry;

    @Test
    void shouldInjectResourceAttributesVerifier(ResourceAttributesVerifier verifier) {
        assertThat(verifier).isNotNull();
    }

    @Test
    void shouldVerifyCustomResourceAttribute(ResourceAttributesVerifier verifier) {
        // Given
        final var metric = meterRegistry.counter("test.metric");
        assertThatCode(() ->
                verifier.assertResourceAttribute(metric, TAG_APPLICATION_KEY, TAG_APPLICATION_VALUE)
        ).doesNotThrowAnyException();
    }

    @Test
    void shouldFailWhenCustomResourceAttributeMismatch(ResourceAttributesVerifier verifier) {
        // Given
        final var metric = meterRegistry.counter("test.metric");
        // When then
        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() ->
                        verifier.assertResourceAttribute(metric, "nonCommonTagKey", "customValue")
                )
                .withMessageContaining("Resource attribute 'nonCommonTagKey' should have value 'customValue'");
    }
}
