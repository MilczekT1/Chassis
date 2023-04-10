package pl.konradboniecki.chassis.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import pl.konradboniecki.chassis.exceptions.ChassisExceptionHandler;

@Slf4j
@AutoConfiguration
public class DefaultExceptionHandlerAutoConfiguration {

    @Bean
    public ChassisExceptionHandler chassisExceptionHandler() {
        log.info("Initializing ChassisExceptionHandler...");
        return new ChassisExceptionHandler();
    }
}
