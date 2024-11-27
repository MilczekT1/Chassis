package io.github.konradboniecki.chassis.configuration;

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
                "budget.chassis.http-logging.enabled=true",
                "spring.config.import=optional:configserver:"
        }
)
public class HttpLoggingAutoConfigurationCustomConfigEnabledIT {

    @Autowired(required = false)
    private CommonsRequestLoggingFilter loggingFilter;
    @Autowired(required = false)
    private FilterRegistrationBean<CommonsRequestLoggingFilter> filterRegistrationBean;

    @Autowired
    private HttpLoggingProperties loggingConfig;

    @Test
    public void when_config_enabled_then_loggingFilter_is_created() {
        assertThat(loggingFilter).isNotNull();
    }

    @Test
    public void when_config_enabled_filterRegistrationBean_is_created() {
        assertThat(filterRegistrationBean).isNotNull();
        assertThat(filterRegistrationBean.getUrlPatterns().contains(loggingConfig.getUrlPattern()));
    }

    @Test
    public void given_enabled_property_is_true_if_set(){
        assertThat(loggingConfig.isEnabled()).isTrue();
    }
}
