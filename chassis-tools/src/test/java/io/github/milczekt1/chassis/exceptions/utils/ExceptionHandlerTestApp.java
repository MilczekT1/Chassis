package io.github.milczekt1.chassis.exceptions.utils;

import io.github.milczekt1.chassis.configuration.WebServerAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = WebServerAutoConfiguration.class)
public class ExceptionHandlerTestApp {
}
