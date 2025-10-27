package io.github.milczekt1.chassis.testapp;

import io.github.milczekt1.chassis.ChassisApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@ChassisApplication(exclude = MongoAutoConfiguration.class)
public class ChassisTestApp {

    public static void main(String[] args) {
        SpringApplication.run(ChassisTestApp.class, args);
    }
}
