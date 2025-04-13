package io.github.milczekt1.chassis.testapp;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringBootTest
class AutoconfiguredBeansVerifierIT {

    @Autowired
    AutoConfiguredBeanVerifier autoConfiguredBeanVerifier;

    @Test
    void shouldStartWithAllRequiredBeansFromTools() {
        final var throwable = catchThrowable(() -> autoConfiguredBeanVerifier.verifyBeans());
        assertThat(throwable).isNotNull();
    }

}
