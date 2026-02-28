package io.github.milczekt1.chassis.test.observability;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * JUnit 5 extension that injects {@link TraceVerifier} into test methods.
 * <p>
 * Creates a TraceVerifier instance that can be used to assert on
 * distributed tracing behavior.
 * </p>
 *
 * <h3>Example Usage:</h3>
 * <pre>
 * {@code
 * @SpringBootTest
 * @ExtendWith(TraceVerifierExtension.class)
 * class TracingIT {
 *     @Test
 *     void testTracing(TraceVerifier traceVerifier) {
 *         traceVerifier.assertSpanIsValid();
 *         String traceId = traceVerifier.getCurrentTraceId();
 *         traceVerifier.assertTraceIdValid(traceId);
 *     }
 * }
 * }
 * </pre>
 */
public class TraceVerifierExtension implements ParameterResolver {

    @Override
    public boolean supportsParameter(
            ParameterContext parameterContext,
            ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == TraceVerifier.class;
    }

    @Override
    public Object resolveParameter(
            ParameterContext parameterContext,
            ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return new TraceVerifier();
    }
}
