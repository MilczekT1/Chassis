package pl.konradboniecki.chassis.tools;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;

import static lombok.AccessLevel.PRIVATE;

@Component
public class ChassisSecurityBasicAuthHelper {

    @Setter(PRIVATE)
    @Getter
    private String basicAuthHeaderValue;

    public ChassisSecurityBasicAuthHelper(@Value("${spring.security.user.name}") String username,
                                          @Value("${spring.security.user.password}") String password) {
        populateBasicAuthHeaderValue(username, password);
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
