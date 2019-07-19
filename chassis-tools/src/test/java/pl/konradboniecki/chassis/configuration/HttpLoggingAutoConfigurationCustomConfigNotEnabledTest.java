package pl.konradboniecki.chassis.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = HttpLoggingAutoConfiguration.class,
        webEnvironment = WebEnvironment.NONE,
        properties = {
            "budget.chassis.http-logging.includeQueryString=false",
            "budget.chassis.http-logging.includePayload=false",
            "budget.chassis.http-logging.includeHeaders=false",
            "budget.chassis.http-logging.maxPayloadLength=10",
            "budget.chassis.http-logging.urlPattern=/",
            "budget.chassis.http-logging.afterMessagePrefix=CUSTOM AFTER REQUEST: \n",
            "budget.chassis.http-logging.beforeMessagePrefix=CUSTOM BEFORE REQUEST: \n",
            "budget.chassis.http-logging.includeClientInfo=false"
        }
)
public class HttpLoggingAutoConfigurationCustomConfigNotEnabledTest {

    @Autowired(required = false)
    private CommonsRequestLoggingFilter loggingFilter;
    @Autowired(required = false)
    private FilterRegistrationBean filterRegistrationBean;

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
    public void when_config_is_provided_values_are_set() {
        assertAll(
                () -> assertThat(loggingConfig).isNotNull(),
                () -> assertThat(loggingConfig.isIncludeHeaders()).isFalse(),
                () -> assertThat(loggingConfig.isIncludePayload()).isFalse(),
                () -> assertThat(loggingConfig.isIncludeQueryString()).isFalse(),
                () -> assertThat(loggingConfig.getMaxPayloadLength()).isEqualTo(10),
                () -> assertThat(loggingConfig.getUrlPattern()).isEqualTo("/"),
                () -> assertThat(loggingConfig.getAfterMessagePrefix()).isEqualToIgnoringNewLines("CUSTOM AFTER REQUEST: \n"),
                () -> assertThat(loggingConfig.getBeforeMessagePrefix()).isEqualToIgnoringNewLines("CUSTOM BEFORE REQUEST: \n"),
                () -> assertThat(loggingConfig.getIncludeClientInfo()).isFalse()
        );
    }

    @Test
    public void enabled_property_is_false_if_not_explicitly_provided(){
        assertThat(loggingConfig.isEnabled()).isFalse();
    }

}
