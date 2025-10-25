package io.github.milczekt1.chassis.testapp;


import io.github.milczekt1.chassis.testapp.utils.TestAppIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@TestAppIntegrationTest
class AutoconfiguredBeansVerifierIT {

    @Autowired
    AutoConfiguredBeanVerifier autoConfiguredBeanVerifier;

    @Test
    void shouldStartWithAllRequiredBeansFromTools() {
        final var throwable = catchThrowable(() -> autoConfiguredBeanVerifier.verifyBeans());
        assertThat(throwable).isNull();
    }

}
