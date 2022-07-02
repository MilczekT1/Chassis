package pl.konradboniecki.chassis.configuration;


import io.undertow.UndertowOptions;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class WebServerConfiguration {
    @Bean
    @Primary
    public ServletWebServerFactory undertowServletWebServerFactory() {
        UndertowServletWebServerFactory undertow = new UndertowServletWebServerFactory();
        undertow.addBuilderCustomizers(customizer -> customizer.setServerOption(UndertowOptions.ENABLE_HTTP2, true));
        return undertow;
    }
}
