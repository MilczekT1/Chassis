package io.github.milczekt1.chassis.exceptions.utils;

import io.github.milczekt1.chassis.exceptions.BadRequestException;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor
@RestController
@RequestMapping("/exceptions")
public class TestController {

    public static final String BAD_REQUEST_TITLE = "Test message";
    public static final String BAD_REQUEST_PATH = "/exceptions/bad-request";

    @GetMapping("/bad-request")
    public void throwBadRequestException() {
        throw new BadRequestException(BAD_REQUEST_TITLE);
    }

}
