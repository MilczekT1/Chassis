package pl.konradboniecki.chassis.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import pl.konradboniecki.chassis.tools.ChassisSecurityBasicAuthHelper;
import pl.konradboniecki.chassis.tools.HashGenerator;

@Slf4j
@AutoConfiguration
public class ChassisSecurityAutoConfiguration {

    @Bean
    public HashGenerator hashGenerator() {
        log.info("Initializing HashGenerator...");
        return new HashGenerator();
    }
    @Bean
    public ChassisSecurityBasicAuthHelper chassisSecurityBasicAuthHelper(
            @Value("${spring.security.user.name:null}") String username,
            @Value("${spring.security.user.password:null}") String password) {
        log.info("Initializing ChassisSecurityBasicAuthHelper...");
        return new ChassisSecurityBasicAuthHelper(username, password);
    }
}
