package io.github.milczekt1.chassis.testapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.github.milczekt1.chassis.testapp.DummyController.BASE_PATH;

@RestController
@RequestMapping(BASE_PATH)
public class DummyController {

    public static final String BASE_PATH = "/dummy";

    @GetMapping
    public String dummy() {
        return "dummyResponseBody";
    }
}
