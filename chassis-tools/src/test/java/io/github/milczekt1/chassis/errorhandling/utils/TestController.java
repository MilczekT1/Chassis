package io.github.milczekt1.chassis.errorhandling.utils;

import io.github.milczekt1.chassis.errorhandling.exceptions.*;
import jakarta.validation.*;
import jakarta.validation.constraints.Min;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static io.github.milczekt1.chassis.errorhandling.utils.TestController.BASE_PATH;

@Validated
@NoArgsConstructor
@RestController
@RequestMapping(BASE_PATH)
public class TestController {

    public static final String BASE_PATH = "/exceptions";
    public static final String ERROR_TITLE = "Test message";
    public static final String BAD_REQUEST_PATH = "/bad-request";
    public static final String RESOURCE_CREATION_ERROR_PATH = "/bad-request/creation-error";
    public static final String NOT_FOUND_PATH = "/not-found";
    public static final String INTERNAL_SERVER_ERROR_PATH = "/internal-server-error";
    public static final String RESOURCE_CONFLICT_PATH = "/conflict";
    public static final String VALIDATION_PATH = "/validation";
    public static final String METHOD_ARG_NOT_VALID_PATH = "/validation/method-argument-not-valid";
    public static final String PARAM_NOT_VALID_PATH = "/validation/param-not-valid";

    @GetMapping(BAD_REQUEST_PATH)
    public void throwBadRequestException() {
        throw new BadRequestException(ERROR_TITLE);
    }

    @GetMapping(RESOURCE_CREATION_ERROR_PATH)
    public void throwResourceCreationException() {
        throw new ResourceCreationException(ERROR_TITLE);
    }

    @GetMapping(NOT_FOUND_PATH)
    public void throwResourceNotFoundException() {
        throw new ResourceNotFoundException(ERROR_TITLE);
    }

    @GetMapping(INTERNAL_SERVER_ERROR_PATH)
    public void throwInternalServerErrorException() {
        throw new InternalServerErrorException(ERROR_TITLE);
    }

    @GetMapping(RESOURCE_CONFLICT_PATH)
    public void throwResourceConflictException() {
        throw new ResourceConflictException(ERROR_TITLE);
    }

    @PostMapping(METHOD_ARG_NOT_VALID_PATH)
    public ResponseEntity<Object> onInvalidBody(@RequestBody @Valid ValidableObject validableObject) {
        return null;
    }

    @PostMapping(PARAM_NOT_VALID_PATH)
    public ResponseEntity<Object> onInvalidRequestParam(@RequestParam(name = "age") /*@Valid*/ @Min(18) Long age) {
        return null;
    }

    @PostMapping(VALIDATION_PATH)
    public void throwConstraintValidationException(@RequestBody final ValidableObject user) {
        final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        final Set<ConstraintViolation<ValidableObject>> violations = validator.validate(user);
        throw new ConstraintViolationException(violations);
    }
}
