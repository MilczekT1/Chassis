package io.github.milczekt1.chassis.testapp;

import io.github.milczekt1.chassis.errorhandling.ChassisExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
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
            applicationContext.getBean(ChassisExceptionHandler.class);
            log.info("All beans provided by Chassis have been initialized.");
        } catch (NoSuchBeanDefinitionException e) {
            throw new IllegalStateException("At least on of chassis based beans is no set", e);
        }
    }
}
