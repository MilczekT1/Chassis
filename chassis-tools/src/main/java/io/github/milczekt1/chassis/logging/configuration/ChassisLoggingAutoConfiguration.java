package io.github.milczekt1.chassis.logging.configuration;

import io.github.milczekt1.chassis.logging.ChassisRequestLoggingInterceptor;
import io.github.milczekt1.chassis.logging.ChassisResponseLoggingAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@AutoConfiguration
@ConditionalOnClass(WebMvcConfigurer.class)
public class ChassisLoggingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ChassisRequestLoggingInterceptor chassisRequestLoggingInterceptor() {
        log.info("Initializing chassisRequestLoggingInterceptor...");
        return new ChassisRequestLoggingInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    public ChassisResponseLoggingAdvice chassisResponseLoggingAdvice() {
        log.info("Initializing chassisResponseLoggingAdvice...");
        return new ChassisResponseLoggingAdvice();
    }

    @Bean
    @ConditionalOnMissingBean(name = "chassisLoggingWebMvcConfigurer")
    public WebMvcConfigurer chassisLoggingWebMvcConfigurer(ChassisRequestLoggingInterceptor interceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(interceptor);
            }
        };
    }

}
