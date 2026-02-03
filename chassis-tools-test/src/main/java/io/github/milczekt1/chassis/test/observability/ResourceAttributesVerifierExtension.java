package io.github.milczekt1.chassis.test.observability;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * JUnit 5 extension that injects {@link ResourceAttributesVerifier} into test methods.
 * <p>
 * Automatically resolves {@link MeterRegistry} from Spring context and
 * creates a ResourceAttributesVerifier instance.
 * </p>
 *
 * <h3>Example Usage:</h3>
 * <pre>
 * {@code
 * @SpringBootTest
 * @ExtendWith(ResourceAttributesVerifierExtension.class)
 * class ObservabilityIT {
 *     @Test
 *     void testResourceAttributes(ResourceAttributesVerifier verifier) {
 *         verifier.assertServiceName("my-service")
 *                 .assertServiceNamespace("production");
 *     }
 * }
 * }
 * </pre>
 */
public class ResourceAttributesVerifierExtension implements ParameterResolver {

    @Override
    public boolean supportsParameter(
            ParameterContext parameterContext,
            ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == ResourceAttributesVerifier.class;
    }

    @Override
    public Object resolveParameter(
            ParameterContext parameterContext,
            ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return new ResourceAttributesVerifier();
    }
}
