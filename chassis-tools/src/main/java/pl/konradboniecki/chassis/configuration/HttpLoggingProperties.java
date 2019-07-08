package pl.konradboniecki.chassis.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "budget.chassis.http-logging")
public class HttpLoggingProperties {

    private boolean enabled = false;
    private boolean includeQueryString = true;
    private boolean includePayload = true;
    private boolean includeHeaders = false;
    private int maxPayloadLength = 10000;
    private String urlPattern = "/api/*";
}
