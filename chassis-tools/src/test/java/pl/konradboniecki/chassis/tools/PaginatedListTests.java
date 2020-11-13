package pl.konradboniecki.chassis.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class PaginatedListTests {

    private Page<LocalDateTime> examplePage;

    @BeforeAll
    public void setup() {
        Pageable pageable = PageRequest.of(1, 1);
        LocalDateTime time1 = LocalDateTime.MAX;
        LocalDateTime time2 = LocalDateTime.MIN;
        List<LocalDateTime> times = new LinkedList<>();
        times.add(time1);
        times.add(time2);
        examplePage = new PageImpl<>(times, pageable, 2);
    }

    @Test
    public void init_when_page_is_null_then_throw_npe() {
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

    @ParameterizedTest
    @MethodSource("examplePages")
    public void when_page(List<LocalDateTime> listOfContent, int offset, int limit, int totalPages) {
        // Given:
        Pageable pageable = PageRequest.of(offset, limit);
        Page<LocalDateTime> pageWithTimes = new PageImpl<>(listOfContent, pageable, listOfContent.size());
        PaginatedList<LocalDateTime> list = new PaginatedList<>(pageWithTimes);
        // When:
        PaginationMetadata pagMeta = list.getPaginationMetadata();
        // Then:
        Assertions.assertAll(
                () -> assertThat(pagMeta.getPageSize()).isEqualTo(limit),
                () -> assertThat(pagMeta.getPage()).isEqualTo(offset),
                () -> assertThat(pagMeta.getTotalElements()).isEqualTo((limit * offset) + pagMeta.getElements()),
                () -> assertThat(pagMeta.getElements()).isEqualTo(listOfContent.size()),
                () -> assertThat(pagMeta.getTotalPages()).isEqualTo(totalPages)
        );
    }

    private static Stream<Arguments> examplePages() {
        List<LocalDateTime> times = new LinkedList<>();
        times.add(LocalDateTime.now());
        times.add(LocalDateTime.now());
        times.add(LocalDateTime.now());
        return Stream.of(
                //         content, offset,  limit,  totalPages
                Arguments.of(times, 0, 1, 3),
                Arguments.of(times, 0, 100, 1),
                Arguments.of(times, 1, 100, 2),
                Arguments.of(Collections.emptyList(), 0, 1, 0)
        );
    }
}
