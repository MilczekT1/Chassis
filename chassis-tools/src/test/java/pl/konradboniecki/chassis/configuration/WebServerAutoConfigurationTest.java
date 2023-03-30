package pl.konradboniecki.chassis.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = WebServerAutoConfiguration.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.config.import=optional:configserver:"
        }
)
class WebServerAutoConfigurationTest {

  @Autowired(required = false)
  ServletWebServerFactory servletWebServerFactory;

    @Test
    void givenAutoconfiguration_whenContextLoads_thenUnderTowWebserverIsInitialized() {
        // Then:
        assertThat(servletWebServerFactory)
            .isNotNull()
            .isInstanceOf(UndertowServletWebServerFactory.class);
    }
}
