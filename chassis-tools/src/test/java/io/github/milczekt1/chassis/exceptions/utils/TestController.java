package io.github.milczekt1.chassis.exceptions.utils;

import io.github.milczekt1.chassis.exceptions.BadRequestException;
import io.github.milczekt1.chassis.exceptions.ResourceNotFoundException;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor
@RestController
@RequestMapping("/exceptions")
public class TestController {

    public static final String ERROR_TITLE = "Test message";
    public static final String BAD_REQUEST_PATH = "/exceptions/bad-request";
    public static final String NOT_FOUND_PATH = "/exceptions/not-found";

    @GetMapping("/bad-request")
    public void throwBadRequestException() {
        throw new BadRequestException(ERROR_TITLE);
    }

    @GetMapping("/not-found")
    public void throwResourceNotFoundException() {
        throw new ResourceNotFoundException(ERROR_TITLE);
    }

}
