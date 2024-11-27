package io.github.milczekt1.chassis.test;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestContextAnnotationUtils;

import java.lang.annotation.Annotation;
import java.util.Optional;

import static java.util.Optional.ofNullable;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestContextUtils {

    public static <T> T getBean(TestContext testContext, Class<T> beanClass) {
        return testContext.getApplicationContext().getBean(beanClass);
    }

    public static <T> Optional<T> getOptionalBean(TestContext testContext, Class<T> beanClass) {
        var beanProvider = testContext.getApplicationContext().getBeanProvider(beanClass);
        return Optional.ofNullable(beanProvider.getIfAvailable());
    }

    public static <A extends Annotation> Optional<A> getTestClassAnnotation(
            TestContext testContext, Class<A> annotation
    ) {
        var descriptor = TestContextAnnotationUtils.findAnnotationDescriptor(testContext.getTestClass(), annotation);
        return ofNullable(descriptor)
                .map(TestContextAnnotationUtils.AnnotationDescriptor::getAnnotation);
    }

    public static <A extends Annotation> Optional<A> getMethodAnnotation(
            TestContext testContext,
            Class<A> annotationClass
    ) {
        return ofNullable(AnnotatedElementUtils.findMergedAnnotation(testContext.getTestMethod(), annotationClass));
    }
}
