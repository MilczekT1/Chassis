package pl.konradboniecki.chassis.tools;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class PaginationMetadataTests {

    @Test
    public void when_null_page_when_creating_object_then_throw_npe() {
        // When:
        Throwable npe = Assertions.catchThrowable(() -> new PaginationMetadata(null));
        // Then:
        assertThat(npe).isInstanceOf(NullPointerException.class);
    }
}
