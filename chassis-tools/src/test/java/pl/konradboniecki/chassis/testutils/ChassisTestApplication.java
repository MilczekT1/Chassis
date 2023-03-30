package pl.konradboniecki.chassis.testutils;

import org.springframework.boot.SpringApplication;
import pl.konradboniecki.chassis.ChassisApplication;

@ChassisApplication
public class ChassisTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChassisTestApplication.class, args);
    }
}
