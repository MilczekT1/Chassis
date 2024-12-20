package io.github.milczekt1.chassis.test.listeners;

import io.github.milczekt1.chassis.test.TestContextUtils;
import io.github.milczekt1.chassis.test.annotations.ControllerTest;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;

/**
 * ControllerTestExecutionListener configures Rest-assured as follows:
 * 1. Default UTF-8 Encoder for json content type.
 * 2. Apply retrieved context path to Rest-assured to eliminate the need to provide it in the test methods
 * 3. Invoke RestAssuredMockMvc.reset() after test class.
 */
@Slf4j
public class ControllerTestExecutionListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestClass(TestContext testContext) {
        var controllerTest = TestContextUtils.getTestClassAnnotation(testContext, ControllerTest.class);
        if (controllerTest.isEmpty()) {
            log.warn("No @ControllerTest annotations present");
            return;
        }
        RestAssuredMockMvc.config = RestAssuredMockMvc.config()
                .encoderConfig(EncoderConfig.encoderConfig().defaultCharsetForContentType("UTF-8", ContentType.JSON));
        RestAssuredMockMvc.basePath = controllerTest.get().basePath();
        RestAssuredMockMvc.mockMvc(TestContextUtils.getBean(testContext, MockMvc.class));
    }

    @Override
    public void afterTestClass(TestContext testContext) {
        var controllerTest = TestContextUtils.getTestClassAnnotation(testContext, ControllerTest.class);
        if (controllerTest.isEmpty()) {
            return;
        }
        RestAssuredMockMvc.reset();
    }
}
