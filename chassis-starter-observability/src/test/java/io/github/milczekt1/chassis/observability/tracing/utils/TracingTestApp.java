package io.github.milczekt1.chassis.observability.tracing.utils;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TracingTestApp {

    public static void main(final String[] args) {
        SpringApplication.run(TracingTestApp.class, args);
    }
}
