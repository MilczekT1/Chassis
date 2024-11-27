package io.github.milczekt1.chassis.testutils;

import io.github.milczekt1.chassis.ChassisApplication;
import org.springframework.boot.SpringApplication;

@ChassisApplication
public class ChassisTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChassisTestApplication.class, args);
    }
}
