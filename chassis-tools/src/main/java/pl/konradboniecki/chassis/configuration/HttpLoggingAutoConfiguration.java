package pl.konradboniecki.chassis.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
@EnableConfigurationProperties(HttpLoggingProperties.class)
public class HttpLoggingAutoConfiguration {

    @Autowired
    private HttpLoggingProperties settings;

    @Bean
    @ConditionalOnProperty(prefix = "budget.chassis.http-logging",
            value = "enabled", havingValue = "true")
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(settings.isIncludeQueryString());
        filter.setIncludePayload(settings.isIncludePayload());
        filter.setMaxPayloadLength(settings.getMaxPayloadLength());
        filter.setIncludeHeaders(settings.isIncludeHeaders());
        filter.setAfterMessagePrefix(settings.getAfterMessagePrefix());
        filter.setBeforeMessagePrefix(settings.getBeforeMessagePrefix());
        filter.setIncludeClientInfo(settings.getIncludeClientInfo());
        return filter;
    }

    @Bean
    @ConditionalOnProperty(prefix = "budget.chassis.http-logging",
            value = "enabled", havingValue = "true")
    public FilterRegistrationBean loggingFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean(logFilter());
        registration.addUrlPatterns(settings.getUrlPattern());
        return registration;
    }
}

