package io.github.milczekt1.chassis.testapp;

import io.github.milczekt1.chassis.errorhandling.ChassisExceptionHandler;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class AutoConfiguredBeanVerifier {

    private ApplicationContext applicationContext;

    @EventListener(ApplicationReadyEvent.class)
    public void verifyBeans() {
        try {
            applicationContext.getBean(ChassisExceptionHandler.class);
        } catch (NoSuchBeanDefinitionException e) {
            throw new IllegalStateException("At least on of chassis based beans is no set", e);
        }
    }
}
