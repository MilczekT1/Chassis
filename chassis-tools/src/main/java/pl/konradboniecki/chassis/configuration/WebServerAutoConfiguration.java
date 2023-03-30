package pl.konradboniecki.chassis.configuration;


import io.undertow.UndertowOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@Slf4j
@AutoConfiguration
public class WebServerAutoConfiguration {
    @Bean
    @Primary
    public ServletWebServerFactory undertowServletWebServerFactory() {
        log.info("Initializing Undertow webserver...");
        UndertowServletWebServerFactory undertow = new UndertowServletWebServerFactory();
        undertow.addBuilderCustomizers(customizer -> customizer.setServerOption(UndertowOptions.ENABLE_HTTP2, true));
        return undertow;
    }
}
