package io.github.konradboniecki.chassis.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@AutoConfiguration
@EnableConfigurationProperties(HttpLoggingProperties.class)
public class HttpLoggingAutoConfiguration {

    @Autowired
    private HttpLoggingProperties httpLoggingProperties;

    @Bean
    @ConditionalOnProperty(prefix = "budget.chassis.http-logging",
            value = "enabled", havingValue = "true")
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(httpLoggingProperties.isIncludeQueryString());
        filter.setIncludePayload(httpLoggingProperties.isIncludePayload());
        filter.setMaxPayloadLength(httpLoggingProperties.getMaxPayloadLength());
        filter.setIncludeHeaders(httpLoggingProperties.isIncludeHeaders());
        filter.setAfterMessagePrefix(httpLoggingProperties.getAfterMessagePrefix());
        filter.setBeforeMessagePrefix(httpLoggingProperties.getBeforeMessagePrefix());
        filter.setIncludeClientInfo(httpLoggingProperties.getIncludeClientInfo());
        return filter;
    }

    @Bean
    @ConditionalOnProperty(prefix = "budget.chassis.http-logging",
            value = "enabled", havingValue = "true")
    public FilterRegistrationBean<CommonsRequestLoggingFilter> loggingFilterRegistration() {
        FilterRegistrationBean<CommonsRequestLoggingFilter> registration = new FilterRegistrationBean<>(logFilter());
        registration.addUrlPatterns(httpLoggingProperties.getUrlPattern());
        return registration;
    }
}

