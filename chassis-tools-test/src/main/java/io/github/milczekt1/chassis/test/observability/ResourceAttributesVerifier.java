package io.github.milczekt1.chassis.test.observability;

import io.micrometer.core.instrument.Meter;
import lombok.NoArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Utility for verifying OpenTelemetry resource attributes in tests.
 * <p>
 * Resource attributes (service.name, service.namespace, deployment.environment)
 * should be applied as common tags to all metrics. This verifier checks that
 * these attributes are correctly configured.
 * </p>
 * <p>
 * Use with {@link ResourceAttributesVerifierExtension} to inject into test methods.
 * </p>
 *
 * <h3>Example Usage:</h3>
 * <pre>
 * {@code
 * @ExtendWith(ResourceAttributesVerifierExtension.class)
 * class ObservabilityIT {
 *     @Test
 *     void testResourceAttributes(ResourceAttributesVerifier verifier) {
 *         verifier.assertServiceName("my-service")
 *                 .assertServiceNamespace("production")
 *                 .assertDeploymentEnvironment("prod");
 *     }
 * }
 * }
 * </pre>
 */
@NoArgsConstructor
public class ResourceAttributesVerifier {

    /**
     * Verify a specific resource attribute is applied as a common tag.
     * <p>
     * Creates a test counter to verify the attribute is present as a tag.
     * </p>
     *
     * @param key           the resource attribute key
     * @param expectedValue the expected value
     * @return this verifier for chaining
     */
    public ResourceAttributesVerifier assertResourceAttribute(Meter meter, String key, String expectedValue) {
        assertThat(meter.getId().getTags())
                .as("Resource attribute '%s' should have value '%s'", key, expectedValue)
                .anyMatch(tag -> tag.getKey().equals(key) && tag.getValue().equals(expectedValue));
        return this;
    }
}
