package io.github.milczekt1.chassis.test.observability;

import io.github.milczekt1.chassis.test.utils.TestToolsIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(TraceVerifierExtension.class)
@TestToolsIntegrationTest
class TraceVerifierExtensionIT {

    @Test
    void shouldInjectTraceVerifier(TraceVerifier traceVerifier) {
        assertThat(traceVerifier).isNotNull();
    }

    @Test
    void shouldValidateTraceIdFormat(TraceVerifier traceVerifier) {
        // Given - valid trace ID (32 hex characters)
        String validTraceId = "aaaabbbbccccdddd1111222233334444";

        // When then
        assertThatCode(() ->
                traceVerifier.assertTraceIdValid(validTraceId)
        ).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @MethodSource("invalidTraceIdFormat")
    void shouldFailOnInvalidTraceIdFormat(String invalidTraceIdFormat, TraceVerifier traceVerifier) {
        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() -> traceVerifier.assertTraceIdValid(invalidTraceIdFormat))
                .withMessageContaining("32 hex characters");
    }

    private static Stream<Arguments> invalidTraceIdFormat() {
        return Stream.of(
                Arguments.of("aabbccdd"), // too short
                Arguments.of("aaaabbbbccccdddd11112222333344445555"), // too long
                Arguments.of("gggghhhhiiiijjjjkkkkllllmmmmnnnn") // nonHex
        );
    }

    @Test
    void shouldValidateSpanIdFormat(TraceVerifier traceVerifier) {
        // given
        String validSpanId = "0011223344556677";

        // When then
        assertThatCode(() ->
                traceVerifier.assertSpanIdValid(validSpanId)
        ).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @MethodSource("invalidSpanIdFormat")
    void shouldFailOnInvalidSpanIdFormat(String invalidSpanIdFormat, TraceVerifier traceVerifier) {
        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() -> traceVerifier.assertSpanIdValid(invalidSpanIdFormat))
                .withMessageContaining("16 hex characters");
    }

    private static Stream<Arguments> invalidSpanIdFormat() {
        return Stream.of(
                Arguments.of("aabbccdd"), // too short
                Arguments.of("00112233445566778899"), // too long
                Arguments.of("gggghhhhiiiijjjj") // nonHex
        );
    }

    @Test
    void shouldExtractTraceIdFromTraceparent(TraceVerifier traceVerifier) {
        // Given
        String validW3CTraceparentHeader = "00-aaaabbbbccccdddd1111222233334444-0011223344556677-01";

        // When
        String extractedTraceId = traceVerifier.extractTraceIdFromTraceparent(validW3CTraceparentHeader);

        // Then
        assertThat(extractedTraceId).isEqualTo("aaaabbbbccccdddd1111222233334444");
    }

    @Test
    void shouldExtractSpanIdFromTraceparent(TraceVerifier traceVerifier) {
        // Given
        String validW3CTraceparentHeader = "00-aaaabbbbccccdddd1111222233334444-0011223344556677-01";

        // When
        String extractedSpanId = traceVerifier.extractSpanIdFromTraceparent(validW3CTraceparentHeader);

        // Then
        assertThat(extractedSpanId).isEqualTo("0011223344556677");
    }

    @Test
    void shouldFailOnInvalidTraceparentFormat(TraceVerifier traceVerifier) {
        // Given
        String invalidTraceparent = "invalid-format";

        // When Then
        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() ->
                        traceVerifier.extractTraceIdFromTraceparent(invalidTraceparent)
                )
                .withMessageContaining("W3C format");
    }

    @Test
    void shouldVerifyResponseHasTraceHeaders(TraceVerifier traceVerifier) {
        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.add("Trace-Id", "aaaabbbbccccdddd1111222233334444");
        headers.add("Span-Id", "0011223344556677");
        ResponseEntity<String> response = new ResponseEntity<>("test", headers, HttpStatus.OK);

        // When/Then - verify headers present and valid
        assertThatCode(() ->
                traceVerifier.assertResponseHasValidTraceHeaders(response)
        ).doesNotThrowAnyException();
    }

    @Test
    void shouldFailWhenResponseMissingTraceHeaders(TraceVerifier traceVerifier) {
        // Given
        ResponseEntity<String> response = ResponseEntity.ok("test");

        // When Then
        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() ->
                        traceVerifier.assertResponseHasValidTraceHeaders(response)
                )
                .withMessageContaining("Trace-Id");
    }

    @Test
    void shouldFailWhenResponseHasInvalidTraceIdFormat(TraceVerifier traceVerifier) {
        // Given - response with invalid trace ID format
        HttpHeaders headers = new HttpHeaders();
        headers.add("Trace-Id", "invalid");
        headers.add("Span-Id", "0011223344556677");
        ResponseEntity<String> response = new ResponseEntity<>("test", headers, HttpStatus.OK);

        // When/Then - verify assertion fails
        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() ->
                        traceVerifier.assertResponseHasValidTraceHeaders(response)
                )
                .withMessageContaining("32 hex characters");
    }
}

