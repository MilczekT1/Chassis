package pl.konradboniecki.chassis.testutils;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"pl.konradboniecki"})
public class ChassisTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChassisTestApplication.class, args);
    }
}
