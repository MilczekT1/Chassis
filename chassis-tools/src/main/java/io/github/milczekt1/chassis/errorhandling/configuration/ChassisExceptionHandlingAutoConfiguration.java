package io.github.milczekt1.chassis.errorhandling.configuration;

import io.github.milczekt1.chassis.errorhandling.ChassisExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@Slf4j
@AutoConfiguration
public class ChassisExceptionHandlingAutoConfiguration {

    @Bean
    public ChassisExceptionHandler chassisExceptionHandler() {
        log.info("Initializing ChassisExceptionHandler...");
        return new ChassisExceptionHandler();
    }
}
