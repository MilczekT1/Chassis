package io.github.milczekt1.chassis.test.logging;

import org.junit.jupiter.api.extension.*;
import org.springframework.beans.factory.annotation.Qualifier;

import static java.util.Optional.ofNullable;

public class LogbackVerifierExtension implements AfterTestExecutionCallback, ParameterResolver {

    public static final String LOGGER_NAME = "io.github.milczekt1";
    private LogVerifier logVerifier;

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        ofNullable(logVerifier).ifPresent(LogVerifier::reset);
    }

    @Override
    public boolean supportsParameter(
            ParameterContext parameterContext, ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == LogVerifier.class;
    }

    @Override
    public Object resolveParameter(
            ParameterContext parameterContext, ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        var loggerName = parameterContext.findAnnotation(Qualifier.class)
                .map(Qualifier::value)
                .orElse(LOGGER_NAME);
        this.logVerifier = new LogVerifier(loggerName);
        this.logVerifier.start();
        return this.logVerifier;
    }
}
