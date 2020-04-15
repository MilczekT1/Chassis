package pl.konradboniecki.chassis.tools;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ChassisSecurityBasicAuthHelperTest {

    @Test
    void getBasicAuthHeaderValue() {
        // Given:
        ChassisSecurityBasicAuthHelper chassisSecurityBasicAuthHelper = new ChassisSecurityBasicAuthHelper("username", "password");
        // When:
        String basicAuthHeaderValue = chassisSecurityBasicAuthHelper.getBasicAuthHeaderValue();
        // Then:
        Assertions.assertThat(basicAuthHeaderValue).isEqualTo("Basic dXNlcm5hbWU6cGFzc3dvcmQ=");
    }

    @Test
    void givenInvalidBACredentialsWhengetBAHeaderValue_thenReturnNull() {
        // Given:
        ChassisSecurityBasicAuthHelper chassisSecurityBasicAuthHelper = new ChassisSecurityBasicAuthHelper(null, null);
        // When:
        String basicAuthHeaderValue = chassisSecurityBasicAuthHelper.getBasicAuthHeaderValue();
        // Then:
        Assertions.assertThat(basicAuthHeaderValue).isEqualTo(null);
    }
}
