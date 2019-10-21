package pl.konradboniecki.chassis.tools;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class PaginatedListTests {

    private Page<LocalDateTime> examplePage;

    @BeforeAll
    public void setup() {
        Pageable pageable = PageRequest.of(0, 100);
        LocalDateTime time1 = LocalDateTime.MAX;
        LocalDateTime time2 = LocalDateTime.MIN;
        examplePage = new PageImpl<>(List.of(time1, time2), pageable, 2);
    }

    @Test
    public void when_null_page_when_creating_object_then_throw_npe() {
        // When:
        Throwable npe = catchThrowable(() -> new PaginatedList<>(null));
        // Then:
        assertThat(npe).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void init_page_content_is_copied() {
        // When:
        PaginatedList<LocalDateTime> list = new PaginatedList<>(examplePage);
        // Then:
        assertThat(list.getItems()).isEqualTo(examplePage.getContent());
    }

    @Test
    public void init_pagination_metadata_is_not_null() {
        // When:
        PaginatedList<LocalDateTime> list = new PaginatedList<>(examplePage);
        //Then:
        PaginationMetadata paginationMetadata = list.getPaginationMetadata();
        assertThat(paginationMetadata).isNotNull();
    }
}
