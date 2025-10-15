package io.github.milczekt1.chassis.test.utils;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication//(excludeName = "io.github.milczekt1.chassis.configuration.WebServerAutoConfiguration")
public class TestApp {

    public static void main(String[] args) {
        SpringApplication.run(TestApp.class, args);
    }
}
