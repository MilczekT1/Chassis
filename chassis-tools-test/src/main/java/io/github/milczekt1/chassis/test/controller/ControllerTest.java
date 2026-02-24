package io.github.milczekt1.chassis.test.controller;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@WebMvcTest
@ActiveProfiles
public @interface ControllerTest {

    String basePath() default "/";

    @AliasFor(annotation = ActiveProfiles.class, attribute = "profiles")
    String[] profiles() default {"itest"};

    @AliasFor(annotation = WebMvcTest.class, attribute = "value")
    Class<?>[] value() default {};

    @AliasFor(annotation = WebMvcTest.class, attribute = "controllers")
    Class<?>[] controllers() default {};

    @AliasFor(annotation = WebMvcTest.class, attribute = "excludeFilters")
    Filter[] excludeFilters() default {};

    @AliasFor(annotation = AutoConfigureMockMvc.class, attribute = "addFilters")
    boolean addFilters() default true;
}
