package pl.konradboniecki.chassis.configuration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.konradboniecki.chassis.exceptions.ChassisExceptionHandler;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = DefaultExceptionHandlerAutoConfiguration.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "spring.config.import=optional:configserver:"
        }
)
class DefaultExceptionHandlerAutoConfigurationTest {

  @Autowired(required = false)
  private ChassisExceptionHandler chassisExceptionHandler;

  @Test
  void chassisExceptionHandlerIsPresent() {
    // Then:
    Assertions.assertThat(chassisExceptionHandler).isNotNull();
  }
}
