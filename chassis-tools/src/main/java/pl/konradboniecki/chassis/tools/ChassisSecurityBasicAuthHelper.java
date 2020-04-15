package pl.konradboniecki.chassis.tools;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
public class ChassisSecurityBasicAuthHelper {

    @Setter(PRIVATE)
    @Getter
    private String basicAuthHeaderValue;

    public ChassisSecurityBasicAuthHelper(
            @Value("${spring.security.user.name:null}") String username,
            @Value("${spring.security.user.password:null}") String password) {
        if (username == null || password == null){
            setBasicAuthHeaderValue(null);
            log.debug("Invalid credentials for Basic Authentication");
        } else {
            populateBasicAuthHeaderValue(username, password);
            log.info("ChassisSecurityBasicAuthHelper initialized.");
        }
    }

    private void populateBasicAuthHeaderValue(String username, String password){
        setBasicAuthHeaderValue("Basic "
                + encodeBasicAuthCredentials(username, password));
    }

    private String encodeBasicAuthCredentials(String username, String password){
        String credentialsBeforeEncoding = username + ":" + password;
        return Base64.getEncoder().encodeToString(credentialsBeforeEncoding.getBytes());
    }
}
