package pl.konradboniecki.chassis.tools;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PaginatedList<T> {

    private List<T> items;
    @JsonProperty("_meta")
    private PaginationMetadata paginationMetadata;

    public PaginatedList() {
    }

    public PaginatedList(@NonNull Page<T> page) {
        this.items = page.getContent();
        this.paginationMetadata = new PaginationMetadata(page);
    }
}
