package io.github.milczekt1.chassis.configuration;

import io.github.milczekt1.chassis.tools.ChassisSecurityBasicAuthHelper;
import io.github.milczekt1.chassis.tools.HashGenerator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = ChassisSecurityAutoConfiguration.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "spring.config.import=optional:configserver:"
        }
)
class ChassisSecurityAutoConfigurationIT {

    @Autowired(required = false)
    private HashGenerator hashGenerator;

    @Autowired(required = false)
    private ChassisSecurityBasicAuthHelper chassisSecurityBasicAuthHelper;


    @Test
    void hashGeneratorIsPresent() {
        // Then:
        Assertions.assertThat(hashGenerator).isNotNull();
    }

    @Test
    void chassisSecurityBasicAuthHelperIsPresent() {
        // Then:
        Assertions.assertThat(chassisSecurityBasicAuthHelper).isNotNull();
    }
}
