package pl.konradboniecki.chassis.tools;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.domain.Page;

@Data
public class PaginationMetadata {

    private long page;
    private long pageSize;
    private long totalPages;
    private long elements;
    private long totalElements;

    public PaginationMetadata() {
    }

    public PaginationMetadata(@NonNull Page page) {
        this.page = page.getNumber();
        this.pageSize = page.getSize();
        this.totalPages = page.getTotalPages();
        this.elements = page.getNumberOfElements();
        this.totalElements = page.getTotalElements();
    }
}
