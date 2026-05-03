package io.github.milczekt1.chassis.testapp;

import io.github.milczekt1.chassis.errorhandling.configuration.ChassisExceptionHandlingAutoConfiguration;
import io.github.milczekt1.chassis.test.controller.ControllerTest;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import static io.github.milczekt1.chassis.testapp.ErrorHandlingDemoController.*;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

@ImportAutoConfiguration(ChassisExceptionHandlingAutoConfiguration.class)
@ControllerTest(value = ErrorHandlingDemoController.class, basePath = ErrorHandlingDemoController.BASE_PATH)
class ErrorHandlingDemoControllerIT {

    @Test
    void shouldReturnProblemDetailForBadRequest() {
        RestAssuredMockMvc.given()
                .when()
                .get(BAD_REQUEST_PATH)
                .then()
                .status(BAD_REQUEST)
                .contentType(APPLICATION_PROBLEM_JSON_VALUE)
                .body("status", equalTo(BAD_REQUEST.value()))
                .body("title", equalTo("Bad Request"));
    }

    @Test
    void shouldReturnProblemDetailForResourceCreationError() {
        RestAssuredMockMvc.given()
                .when()
                .get(CREATION_ERROR_PATH)
                .then()
                .status(BAD_REQUEST)
                .contentType(APPLICATION_PROBLEM_JSON_VALUE)
                .body("status", equalTo(BAD_REQUEST.value()))
                .body("title", equalTo("Bad Request"));
    }

    @Test
    void shouldReturnProblemDetailForResourceNotFound() {
        RestAssuredMockMvc.given()
                .when()
                .get("/not-found/widget/42")
                .then()
                .status(NOT_FOUND)
                .contentType(APPLICATION_PROBLEM_JSON_VALUE)
                .body("status", equalTo(NOT_FOUND.value()))
                .body("title", equalTo("Not Found"));
    }

    @Test
    void shouldReturnProblemDetailForResourceConflict() {
        RestAssuredMockMvc.given()
                .when()
                .get("/conflict/widget/42/archive")
                .then()
                .status(CONFLICT)
                .contentType(APPLICATION_PROBLEM_JSON_VALUE)
                .body("status", equalTo(CONFLICT.value()))
                .body("title", equalTo("Conflict"));
    }

    @Test
    void shouldReturnProblemDetailForInternalServerError() {
        RestAssuredMockMvc.given()
                .when()
                .get(INTERNAL_PATH)
                .then()
                .status(INTERNAL_SERVER_ERROR)
                .contentType(APPLICATION_PROBLEM_JSON_VALUE)
                .body("status", equalTo(INTERNAL_SERVER_ERROR.value()))
                .body("title", equalTo("Internal Server Error"));
    }

    @Test
    void shouldReturnProblemDetailForInvalidRequestBody() {
        final String invalidBody = "{\"quantity\":0}";

        RestAssuredMockMvc.given()
                .contentType(APPLICATION_JSON_VALUE)
                .body(invalidBody)
                .when()
                .post(VALIDATION_BODY_PATH)
                .then()
                .status(BAD_REQUEST)
                .contentType(APPLICATION_PROBLEM_JSON_VALUE)
                .body("status", equalTo(BAD_REQUEST.value()))
                .body("title", equalTo("Bad Request"));
    }

    @Test
    void shouldPassValidationWithValidRequestBody() {
        final String invalidBody = "{\"quantity\":1}";

        RestAssuredMockMvc.given()
                .contentType(APPLICATION_JSON_VALUE)
                .body(invalidBody)
                .when()
                .post(VALIDATION_BODY_PATH)
                .then()
                .status(NO_CONTENT);
    }

    @Test
    void shouldReturnProblemDetailForInvalidRequestParam() {
        RestAssuredMockMvc.given()
                .queryParam("age", 16L)
                .when()
                .get(VALIDATION_QUERY_PATH)
                .then()
                .status(BAD_REQUEST)
                .contentType(APPLICATION_PROBLEM_JSON_VALUE)
                .body("status", equalTo(BAD_REQUEST.value()))
                .body("title", equalTo("Bad Request"));
    }

    @Test
    void shouldPassValidationWithValidRequestParam() {
        RestAssuredMockMvc.given()
                .queryParam("age", 18L)
                .when()
                .get(VALIDATION_QUERY_PATH)
                .then()
                .status(NO_CONTENT);
    }
}
