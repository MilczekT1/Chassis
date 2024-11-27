package io.github.konradboniecki.chassis.testutils;

import io.github.konradboniecki.chassis.ChassisApplication;
import org.springframework.boot.SpringApplication;

@ChassisApplication
public class ChassisTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChassisTestApplication.class, args);
    }
}
