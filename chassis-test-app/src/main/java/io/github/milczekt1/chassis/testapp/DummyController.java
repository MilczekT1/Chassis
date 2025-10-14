package io.github.milczekt1.chassis.testapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/dummy")
public class DummyController {

    @GetMapping
    public String dummy() {
        return "dummy";
    }
}
