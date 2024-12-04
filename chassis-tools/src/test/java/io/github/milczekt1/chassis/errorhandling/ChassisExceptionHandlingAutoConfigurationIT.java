package io.github.milczekt1.chassis.errorhandling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.milczekt1.chassis.errorhandling.utils.ChassisErrorHandlerIntegrationTest;
import io.github.milczekt1.chassis.errorhandling.utils.TestController;
import io.github.milczekt1.chassis.errorhandling.utils.ValidableObject;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.spec.internal.RegexPatterns;
import org.springframework.test.web.servlet.MockMvc;

import static io.github.milczekt1.chassis.errorhandling.utils.TestController.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

@ChassisErrorHandlerIntegrationTest
class ChassisExceptionHandlingAutoConfigurationIT {

    @Autowired(required = false)
    private ChassisExceptionHandler chassisExceptionHandler;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mvc);
        RestAssuredMockMvc.basePath = TestController.BASE_PATH;
    }

  @Test
  void chassisExceptionHandlerIsPresent() {
    // Then:
    Assertions.assertThat(chassisExceptionHandler).isNotNull();
  }

    @Test
    void shouldReturnValidProblemDetailsForBadRequestException() {
        RestAssuredMockMvc.given()
                .when()
                .get(BAD_REQUEST_PATH)
                .then()
                .status(BAD_REQUEST)
                .contentType(APPLICATION_PROBLEM_JSON_VALUE)
                .body("type", equalTo("about:blank"))
                .body("title", equalTo("Bad Request"))
                .body("status", equalTo(BAD_REQUEST.value()))
                .body("detail", equalTo(TestController.ERROR_TITLE))
                .body("instance", equalTo(BASE_PATH + BAD_REQUEST_PATH))
                .body("timestamp", matchesPattern(RegexPatterns.iso8601WithOffset().getPattern()));
    }

    @Test
    void shouldReturnValidProblemDetailsForResourceNotFoundException() {
        RestAssuredMockMvc.given()
                .when()
                .get(NOT_FOUND_PATH)
                .then()
                .status(NOT_FOUND)
                .contentType(APPLICATION_PROBLEM_JSON_VALUE)
                .body("type", equalTo("about:blank"))
                .body("title", equalTo("Not Found"))
                .body("status", equalTo(NOT_FOUND.value()))
                .body("detail", equalTo(TestController.ERROR_TITLE))
                .body("instance", equalTo(BASE_PATH + NOT_FOUND_PATH))
                .body("timestamp", matchesPattern(RegexPatterns.iso8601WithOffset().getPattern()));
    }

    @Test
    void shouldReturnValidProblemDetailsForInternalServerErrorException() {
        RestAssuredMockMvc.given()
                .when()
                .get(INTERNAL_SERVER_ERROR_PATH)
                .then()
                .status(INTERNAL_SERVER_ERROR)
                .contentType(APPLICATION_PROBLEM_JSON_VALUE)
                .body("type", equalTo("about:blank"))
                .body("title", equalTo("Internal Server Error"))
                .body("status", equalTo(INTERNAL_SERVER_ERROR.value()))
                .body("detail", equalTo(TestController.ERROR_TITLE))
                .body("instance", equalTo(BASE_PATH + INTERNAL_SERVER_ERROR_PATH))
                .body("timestamp", matchesPattern(RegexPatterns.iso8601WithOffset().getPattern()));
    }

    @Test
    void shouldReturnValidProblemDetailsForException() {
        RestAssuredMockMvc.given()
                .when()
                .get(INTERNAL_SERVER_ERROR_DEFAULT_PATH)
                .then()
                .status(INTERNAL_SERVER_ERROR)
                .contentType(APPLICATION_PROBLEM_JSON_VALUE)
                .body("type", equalTo("about:blank"))
                .body("title", equalTo("Internal Server Error"))
                .body("status", equalTo(INTERNAL_SERVER_ERROR.value()))
                .body("detail", equalTo(TestController.ERROR_TITLE))
                .body("instance", equalTo(BASE_PATH + INTERNAL_SERVER_ERROR_DEFAULT_PATH))
                .body("timestamp", matchesPattern(RegexPatterns.iso8601WithOffset().getPattern()));
    }

    @Test
    void shouldReturnValidProblemDetailsForResourceConflictException() {
        RestAssuredMockMvc.given()
                .when()
                .get(RESOURCE_CONFLICT_PATH)
                .then()
                .status(CONFLICT)
                .contentType(APPLICATION_PROBLEM_JSON_VALUE)
                .body("type", equalTo("about:blank"))
                .body("title", equalTo("Conflict"))
                .body("status", equalTo(CONFLICT.value()))
                .body("detail", equalTo(TestController.ERROR_TITLE))
                .body("instance", equalTo(BASE_PATH + RESOURCE_CONFLICT_PATH))
                .body("timestamp", matchesPattern(RegexPatterns.iso8601WithOffset().getPattern()));
    }

    @Test
    void shouldReturnValidProblemDetailsForResourceCreationException() {
        RestAssuredMockMvc.given()
                .when()
                .get(RESOURCE_CREATION_ERROR_PATH)
                .then()
                .status(BAD_REQUEST)
                .contentType(APPLICATION_PROBLEM_JSON_VALUE)
                .body("type", equalTo("about:blank"))
                .body("title", equalTo("Bad Request"))
                .body("status", equalTo(BAD_REQUEST.value()))
                .body("detail", equalTo(TestController.ERROR_TITLE))
                .body("instance", equalTo(BASE_PATH + RESOURCE_CREATION_ERROR_PATH))
                .body("timestamp", matchesPattern(RegexPatterns.iso8601WithOffset().getPattern()));
    }

    @Test
    void shouldReturnValidProblemDetailsForMethodArgumentNotValid() throws JsonProcessingException {
        RestAssuredMockMvc.given()
                .contentType(APPLICATION_JSON_VALUE)
                .body(new ObjectMapper().writeValueAsString(new ValidableObject(16L)))
                .when()
                .post(METHOD_ARG_NOT_VALID_PATH)
                .then()
                .status(BAD_REQUEST)
                .contentType(APPLICATION_PROBLEM_JSON_VALUE)
                .body("type", equalTo("about:blank"))
                .body("title", equalTo("Bad Request"))
                .body("status", equalTo(BAD_REQUEST.value()))
                .body("detail", equalTo("Constraint Violation"))
                .body("instance", equalTo(BASE_PATH + METHOD_ARG_NOT_VALID_PATH))
                .body("timestamp", matchesPattern(RegexPatterns.iso8601WithOffset().getPattern()))
                .body("violations", hasSize(1))
                .body("violations[0].field", equalTo("age"))
                .body("violations[0].message", equalTo("must be greater than or equal to 18"));
    }

    @Test
    void shouldReturnValidProblemDetailsForParamNotValid() throws JsonProcessingException {
        RestAssuredMockMvc.given()
                .contentType(APPLICATION_JSON_VALUE)
                .queryParams("age", 16L)
                .when()
                .post(PARAM_NOT_VALID_PATH)
                .then()
                .status(BAD_REQUEST)
                .contentType(APPLICATION_PROBLEM_JSON_VALUE)
                .body("type", equalTo("about:blank"))
                .body("title", equalTo("Bad Request"))
                .body("status", equalTo(BAD_REQUEST.value()))
                .body("detail", equalTo("Constraint Violation"))
                .body("instance", equalTo(BASE_PATH + PARAM_NOT_VALID_PATH))
                .body("timestamp", matchesPattern(RegexPatterns.iso8601WithOffset().getPattern()))
                .body("violations", hasSize(1))
                .body("violations[0].field", equalTo("queryParam"))
                .body("violations[0].message", equalTo("must be greater than or equal to 18"));
    }

    @Test
    void shouldReturnValidProblemDetailsForValidationException() throws JsonProcessingException {
        RestAssuredMockMvc.given()
                .contentType(APPLICATION_JSON_VALUE)
                .body(new ObjectMapper().writeValueAsString(new ValidableObject(16L)))
                .when()
                .post(VALIDATION_PATH)
                .then()
                .status(BAD_REQUEST)
                .contentType(APPLICATION_PROBLEM_JSON_VALUE)
                .body("type", equalTo("about:blank"))
                .body("title", equalTo("Bad Request"))
                .body("status", equalTo(BAD_REQUEST.value()))
                .body("detail", equalTo("Constraint Violation"))
                .body("instance", equalTo(BASE_PATH + VALIDATION_PATH))
                .body("timestamp", matchesPattern(RegexPatterns.iso8601WithOffset().getPattern()))
                .body("violations", hasSize(1))
                .body("violations[0].field", equalTo("age"))
                .body("violations[0].message", equalTo("must be greater than or equal to 18"));
    }

}
