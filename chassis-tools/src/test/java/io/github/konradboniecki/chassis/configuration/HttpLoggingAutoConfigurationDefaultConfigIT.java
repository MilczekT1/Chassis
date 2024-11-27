package io.github.konradboniecki.chassis.configuration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = HttpLoggingAutoConfiguration.class,
        webEnvironment = WebEnvironment.NONE,
        properties = {
                "spring.config.import=optional:configserver:"
        }
)
public class HttpLoggingAutoConfigurationDefaultConfigIT {

    @Autowired(required = false)
    private CommonsRequestLoggingFilter loggingFilter;
    @Autowired(required = false)
    private FilterRegistrationBean<CommonsRequestLoggingFilter> filterRegistrationBean;

    @Autowired
    private HttpLoggingProperties loggingConfig;

    @Test
    public void when_config_not_enabled_loggingFilter_is_not_created() {
        assertThat(loggingFilter).isNull();
    }

    @Test
    public void when_config_not_enabled_filterRegistrationBean_is_not_created() {
        assertThat(filterRegistrationBean).isNull();
    }

    @Test
    public void when_no_config_is_provided_default_configuration_properties_values_are_set(){
        Assertions.assertAll(
                () -> assertThat(loggingConfig).isNotNull(),
                () -> assertThat(loggingConfig.isEnabled()).isFalse(),
                () -> assertThat(loggingConfig.isIncludeHeaders()).isFalse(),
                () -> assertThat(loggingConfig.isIncludePayload()).isTrue(),
                () -> assertThat(loggingConfig.isIncludeQueryString()).isTrue(),
                () -> assertThat(loggingConfig.getMaxPayloadLength()).isEqualTo(10000),
                () -> assertThat(loggingConfig.getUrlPattern()).isEqualTo("/api/*"),
                () -> assertThat(loggingConfig.getAfterMessagePrefix()).isEqualTo("AFTER REQUEST: \n"),
                () -> assertThat(loggingConfig.getBeforeMessagePrefix()).isEqualTo("BEFORE REQUEST: \n"),
                () -> assertThat(loggingConfig.getIncludeClientInfo()).isTrue()
        );
    }
}
