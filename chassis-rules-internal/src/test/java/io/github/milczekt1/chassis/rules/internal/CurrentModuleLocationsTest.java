package io.github.milczekt1.chassis.rules.internal;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrentModuleLocationsTest {

    private static final Path TARGET = Path.of("target").toAbsolutePath();

    private final CurrentModuleLocations provider = new CurrentModuleLocations();

    @Test
    void returnsTheTestClassesOutputAndItsProductionSibling() {
        var locations = provider.get(CurrentModuleLocationsTest.class);

        assertThat(locations).map(location -> Path.of(location.asURI())).containsExactlyInAnyOrder(
                TARGET.resolve("test-classes"),
                TARGET.resolve("classes"));
    }

    @Test
    void rejectsAClassLoadedFromAJar() {
        assertThatThrownBy(() -> provider.get(Test.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("junit-jupiter-api");
    }

    @Test
    void rejectsAProductionClass() {
        assertThatThrownBy(() -> provider.get(CurrentModuleLocations.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("target/test-classes");
    }
}
