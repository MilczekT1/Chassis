package io.github.milczekt1.chassis.logging.utils;

import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static io.github.milczekt1.chassis.logging.utils.TestController.BASE_PATH;

@NoArgsConstructor
@RestController
@RequestMapping(BASE_PATH)
public class TestController {

    public static final String BASE_PATH = "/api/logs";

    @GetMapping
    public ResponseEntity<String> dummyGet(@RequestParam("queryParam") String queryParam) {
        return ResponseEntity.ok("test response body");
    }

    @GetMapping("/withCollection")
    public ResponseEntity<List<String>> collectionGet() {
        return ResponseEntity.ok(List.of("item1", "item2"));
    }
}
