package io.github.milczekt1.chassis.exceptions.utils;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(excludeName = "io.github.milczekt1.chassis.configuration.WebServerAutoConfiguration")
public class ExceptionHandlerTestApp {

    public static void main(String[] args) {
        SpringApplication.run(ExceptionHandlerTestApp.class, args);
    }
}
