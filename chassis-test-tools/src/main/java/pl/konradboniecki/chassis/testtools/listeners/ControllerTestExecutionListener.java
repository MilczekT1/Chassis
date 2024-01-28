package pl.konradboniecki.chassis.testtools.listeners;

import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import pl.konradboniecki.chassis.testtools.TestContextUtils;
import pl.konradboniecki.chassis.testtools.annotations.ControllerTest;

/**
 * RestAssuredTestListener configures Rest-assured as follows:
 * 1. Use same ObjectMapper as in production code
 * 2. Set up web app context to avoid boilerplate code in the test class itself
 * 3. Apply retrieved context path to Rest-assured to eliminate the need to provide it in the test methods
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
        //TODO:  use production context mapper later
        //var objectMapper = TestContextUtils.getBean(testContext, ObjectMapper.class);
        //.objectMapperConfig(new ObjectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> objectMapper))
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
