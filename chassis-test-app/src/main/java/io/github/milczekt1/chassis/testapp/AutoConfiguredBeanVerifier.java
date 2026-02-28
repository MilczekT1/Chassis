package io.github.milczekt1.chassis.testapp;

import io.github.milczekt1.chassis.errorhandling.ChassisExceptionHandler;
import io.github.milczekt1.chassis.errorhandling.configuration.ChassisExceptionHandlingAutoConfiguration;
import io.github.milczekt1.chassis.logging.ChassisRequestLoggingInterceptor;
import io.github.milczekt1.chassis.logging.ChassisResponseLoggingAdvice;
import io.github.milczekt1.chassis.observability.configuration.ChassisObservabilityAutoConfiguration;
import io.github.milczekt1.chassis.observability.configuration.ChassisObservabilityProperties;
import io.github.milczekt1.chassis.observability.tracing.ChassisTraceResponseHeaderFilter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class AutoConfiguredBeanVerifier {

    private ApplicationContext applicationContext;

    @EventListener(ApplicationReadyEvent.class)
    public void verifyBeans() {
        try {
            verifyBeansErrorHandling();
            verifyBeansLogging();
            verifyBeansObservability();
            log.info("All beans provided by Chassis have been initialized.");
        } catch (NoSuchBeanDefinitionException e) {
            throw new IllegalStateException("At least on of chassis based beans is no set", e);
        }
    }

    private void verifyBeansErrorHandling() {
        applicationContext.getBean(ChassisExceptionHandler.class);
        applicationContext.getBean(ChassisExceptionHandlingAutoConfiguration.class);
    }

    private void verifyBeansLogging() {
        applicationContext.getBean(ChassisRequestLoggingInterceptor.class);
        applicationContext.getBean(ChassisResponseLoggingAdvice.class);
    }

    private void verifyBeansObservability() {
        applicationContext.getBean(ChassisObservabilityAutoConfiguration.class);
        applicationContext.getBean(ChassisObservabilityProperties.class);
        applicationContext.getBean("chassisObservabilityMeterRegistryCustomizer", MeterRegistryCustomizer.class);
        applicationContext.getBean(MeterRegistry.class);
        applicationContext.getBean(ChassisTraceResponseHeaderFilter.class);
    }
}
