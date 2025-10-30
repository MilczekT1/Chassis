package io.github.milczekt1.chassis.logging;

import io.github.milczekt1.chassis.logging.utils.LoggingIntegrationTest;
import io.github.milczekt1.chassis.logging.utils.TestController;
import io.github.milczekt1.chassis.test.logging.LogVerifier;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

@LoggingIntegrationTest
public class InterceptorTestIT {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.basePath = TestController.BASE_PATH;
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    void requestLoggingInterceptor_shouldAroundRequest(LogVerifier logVerifier) {
        RestAssuredMockMvc.given()
                .queryParam("queryParam", "testParam")
                .when()
                .get()
                .then()
                .statusCode(200)
                .body(equalTo("test response body"));
        logVerifier.containsInfo("Incoming: method: GET, uri: /api/logs, queryString: ?queryParam=testParam, remoteIp: 127.0.0.1, userAgent: null");
        logVerifier.containsInfo("Request completed. Status: 200, duration: ");
    }

    @Test
    void responseLoggingAdvice_shouldLog_whenResponseBodyIsCollection(LogVerifier logVerifier) {
        RestAssuredMockMvc.given()
                .queryParam("queryParam", "testParam")
                .when()
                .get("withCollection")
                .then()
                .statusCode(200)
                .body("", hasItems("item1", "item2"));

        logVerifier.containsInfo("Collection returned on endpoint: /api/logs/withCollection, size: 2, status: 200");
    }
}
