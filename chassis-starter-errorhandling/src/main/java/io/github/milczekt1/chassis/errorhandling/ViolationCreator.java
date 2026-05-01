package io.github.milczekt1.chassis.errorhandling;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ViolationCreator {

    public static List<Violation> fromConstraintViolations(final Set<ConstraintViolation<?>> constraintViolations) {
        final List<Violation> violations = constraintViolations.stream()
                .map(cv -> new Violation(violationNameFromPropertyPath(cv), cv.getMessage()))
                .toList();

        return sortViolations(violations);
    }

    public static List<Violation> fromBindingResult(final BindingResult bindingResult) {
        final Stream<Violation> fieldErrors = bindingResult.getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()));

        final Stream<Violation> globalErrors = bindingResult.getGlobalErrors().stream()
                .map(error -> new Violation(error.getObjectName(), error.getDefaultMessage()));

        final List<Violation> violations = Stream.concat(fieldErrors, globalErrors)
                .toList();

        return sortViolations(violations);
    }

    private static List<Violation> sortViolations(final List<Violation> violations) {
        return violations.stream()
                .sorted(comparing(Violation::getField)
                        .thenComparing(Violation::getMessage))
                .toList();
    }

    /**
     * Translates methodName.paramName -> paramName.
     */
    private static String violationNameFromPropertyPath(ConstraintViolation<?> cv) {
        final var propertyPathString = cv.getPropertyPath().toString();
        if (propertyPathString.contains(".")) {
            final var violationName = extractViolationName(propertyPathString);
            if (isQueryParam(violationName)) {
                return "queryParam";
            }
            return violationName;
        }
        return propertyPathString;
    }

    private static boolean isQueryParam(final String arg) {
        return arg.startsWith("arg");
    }

    private static String extractViolationName(@NotNull final String propertyPathString) {
        return propertyPathString.split("\\.")[1];
    }
}
