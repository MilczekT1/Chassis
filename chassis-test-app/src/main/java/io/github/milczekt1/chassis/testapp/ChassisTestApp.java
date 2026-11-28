package io.github.milczekt1.chassis.testapp;

import io.github.milczekt1.chassis.ChassisApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestClient;

@EnableAsync
@ChassisApplication
public class ChassisTestApp {

    public static void main(String[] args) {
        SpringApplication.run(ChassisTestApp.class, args);
    }

    @Bean
    @ConditionalOnMissingBean
    public RestClient restClient() {
        return RestClient.create();
    }
}
