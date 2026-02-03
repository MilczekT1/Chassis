package io.github.milczekt1.chassis.testapp;

import io.github.milczekt1.chassis.ChassisApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@EnableAsync
@ChassisApplication
public class ChassisTestApp {

    public static void main(String[] args) {
        SpringApplication.run(ChassisTestApp.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
