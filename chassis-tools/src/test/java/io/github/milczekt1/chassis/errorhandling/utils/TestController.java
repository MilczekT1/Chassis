package io.github.milczekt1.chassis.errorhandling.utils;

import io.github.milczekt1.chassis.errorhandling.exceptions.*;
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
    public static final String RESOURCE_CREATION_ERROR_PATH = "/exceptions/bad-request/creation-error";
    public static final String NOT_FOUND_PATH = "/exceptions/not-found";
    public static final String INTERNAL_SERVER_ERROR_PATH = "/exceptions/internal-server-error";
    public static final String RESOURCE_CONFLICT_ERROR_PATH = "/exceptions/conflict";

    @GetMapping("/bad-request")
    public void throwBadRequestException() {
        throw new BadRequestException(ERROR_TITLE);
    }

    @GetMapping("/bad-request/creation-error")
    public void throwResourceCreationException() {
        throw new ResourceCreationException(ERROR_TITLE);
    }

    @GetMapping("/not-found")
    public void throwResourceNotFoundException() {
        throw new ResourceNotFoundException(ERROR_TITLE);
    }

    @GetMapping("/internal-server-error")
    public void throwInternalServerErrorException() {
        throw new InternalServerErrorException(ERROR_TITLE);
    }

    @GetMapping("/conflict")
    public void throwResourceConflictException() {
        throw new ResourceConflictException(ERROR_TITLE);
    }
}
