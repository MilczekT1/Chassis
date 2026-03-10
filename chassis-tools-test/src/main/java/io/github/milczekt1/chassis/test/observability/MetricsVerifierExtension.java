package io.github.milczekt1.chassis.test.observability;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * JUnit 5 extension that injects {@link MetricsVerifier} into test methods.
 * <p>
 * Automatically resolves {@link MeterRegistry} from Spring context and
 * creates a MetricsVerifier instance.
 * </p>
 *
 * <h3>Example Usage:</h3>
 * <pre>
 * {@code
 * @SpringBootTest
 * @ExtendWith(MetricsVerifierExtension.class)
 * class MetricsIT {
 *     @Test
 *     void testMetrics(MetricsVerifier metricsVerifier) {
 *         metricsVerifier.assertMetric("my.metric").exists();
 *     }
 * }
 * }
 * </pre>
 */
public class MetricsVerifierExtension implements ParameterResolver {

    @Override
    public boolean supportsParameter(
            ParameterContext parameterContext,
            ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == MetricsVerifier.class;
    }

    @Override
    public Object resolveParameter(
            ParameterContext parameterContext,
            ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        ApplicationContext applicationContext = SpringExtension.getApplicationContext(extensionContext);
        MeterRegistry meterRegistry = applicationContext.getBean(MeterRegistry.class);
        return new MetricsVerifier(meterRegistry);
    }
}
