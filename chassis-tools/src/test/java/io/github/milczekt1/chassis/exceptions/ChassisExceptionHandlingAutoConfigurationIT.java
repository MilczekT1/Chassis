package io.github.milczekt1.chassis.exceptions;

import io.github.milczekt1.chassis.exceptions.utils.ChassisErrorHandlerIntegrationTest;
import io.github.milczekt1.chassis.exceptions.utils.TestController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.spec.internal.RegexPatterns;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
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
                .get("/exceptions/bad-request")
                .then()
                .status(BAD_REQUEST)
                .contentType(APPLICATION_PROBLEM_JSON_VALUE)
                .body("type", equalTo("about:blank"))
                .body("title", equalTo("Bad Request"))
                .body("status", equalTo(400))
                .body("detail", equalTo(TestController.BAD_REQUEST_TITLE))
                .body("instance", equalTo(TestController.BAD_REQUEST_PATH))
                .body("timestamp", matchesPattern(RegexPatterns.iso8601WithOffset().getPattern()));
    }

}
