package io.github.milczekt1.chassis.errorhandling.configuration;

import io.github.milczekt1.chassis.errorhandling.ChassisExceptionHandler;
import io.github.milczekt1.chassis.errorhandling.RequestPathInstanceInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.ErrorResponse;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@AutoConfiguration
public class ChassisExceptionHandlingAutoConfiguration {

    @Bean
    public ChassisExceptionHandler chassisExceptionHandler() {
        log.info("Initializing ChassisExceptionHandler...");
        return new ChassisExceptionHandler();
    }

    @Bean
    public WebMvcConfigurer chassisErrorResponseInterceptorsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addErrorResponseInterceptors(List<ErrorResponse.Interceptor> interceptors) {
                interceptors.add(new RequestPathInstanceInterceptor());
            }
        };
    }
}
