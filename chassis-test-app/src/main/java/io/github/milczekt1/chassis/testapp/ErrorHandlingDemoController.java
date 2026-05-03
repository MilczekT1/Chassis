package io.github.milczekt1.chassis.testapp;

import io.github.milczekt1.chassis.errorhandling.ChassisExceptionHandler;
import io.github.milczekt1.chassis.errorhandling.exceptions.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static io.github.milczekt1.chassis.testapp.ErrorHandlingDemoController.BASE_PATH;

/**
 * Controller demonstrating Chassis Error Handling features.
 * <p>
 * This controller showcases every failure mode that the Chassis error-handling
 * starter translates into an RFC 7807 <code>application/problem+json</code>
 * response:
 * <ul>
 *   <li><b>BadRequestException</b> → 400 Bad Request</li>
 *   <li><b>ResourceCreationException</b> → 400 Bad Request</li>
 *   <li><b>ResourceNotFoundException</b> → 404 Not Found</li>
 *   <li><b>ResourceConflictException</b> → 409 Conflict</li>
 *   <li><b>InternalServerErrorException</b> → 500 Internal Server Error</li>
 *   <li><b>Bean Validation on request body</b> → 400 Bad Request with violations array</li>
 *   <li><b>Bean Validation on query param</b> → 400 Bad Request with violations array</li>
 * </ul>
 * </p>
 * <p>
 * All responses come from {@link ChassisExceptionHandler} in the
 * <code>chassis-starter-errorhandling</code> module, which is registered
 * automatically via Spring Boot auto-configuration.
 * </p>
 *
 * @see ChassisExceptionHandler
 */
@Slf4j
@Validated
@RestController
@RequestMapping(BASE_PATH)
public class ErrorHandlingDemoController {

    public static final String BASE_PATH = "/errors";
    public static final String BAD_REQUEST_PATH = "/bad-request";
    public static final String CREATION_ERROR_PATH = "/creation-error";
    public static final String NOT_FOUND_PATH = "/not-found/widget/{id}";
    public static final String CONFLICT_PATH = "/conflict/widget/{id}/archive";
    public static final String INTERNAL_PATH = "/internal";
    public static final String VALIDATION_BODY_PATH = "/validation/body";
    public static final String VALIDATION_QUERY_PATH = "/validation/query";

    @GetMapping(BAD_REQUEST_PATH)
    public ResponseEntity<Void> badRequest() {
        log.info("Demonstrating BadRequestException");
        throw new BadRequestException("Widget 'blue' is not a valid color");
    }

    @GetMapping(CREATION_ERROR_PATH)
    public ResponseEntity<Void> creationError() {
        log.info("Demonstrating ResourceCreationException");
        throw new ResourceCreationException("Widget creation rejected: name must be lowercase");
    }

    @GetMapping(NOT_FOUND_PATH)
    public ResponseEntity<Void> notFound(@PathVariable final String id) {
        log.info("Demonstrating ResourceNotFoundException for widget {}", id);
        throw new ResourceNotFoundException("Widget " + id + " not found");
    }

    @GetMapping(CONFLICT_PATH)
    public ResponseEntity<Void> conflict(@PathVariable final String id) {
        log.info("Demonstrating ResourceConflictException for widget {}", id);
        throw new ResourceConflictException("Widget " + id + " is already archived");
    }

    @GetMapping(INTERNAL_PATH)
    public ResponseEntity<Void> internal() {
        log.info("Demonstrating InternalServerErrorException");
        throw new InternalServerErrorException("Failed to reach downstream pricing service");
    }

    @PostMapping(VALIDATION_BODY_PATH)
    public ResponseEntity<Void> validateBody(@RequestBody @Valid final WidgetRequest request) {
        log.info("Validated body: {}", request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(VALIDATION_QUERY_PATH)
    public ResponseEntity<Void> validateQuery(@RequestParam("age") @Min(18) final Long age) {
        log.info("Validated query age: {}", age);
        return ResponseEntity.noContent().build();
    }

    public record WidgetRequest(@Min(1) Long quantity) {
    }
}
