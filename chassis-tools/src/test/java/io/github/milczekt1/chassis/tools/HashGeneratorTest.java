package io.github.milczekt1.chassis.tools;

import io.github.milczekt1.chassis.exceptions.HashGenerationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HashGeneratorTest {

    private static HashGenerator hashGenerator;

    @BeforeAll
    public static void setUpDefaultTokenGenerator(){
        hashGenerator = new HashGenerator();
    }

    @Test
    public void givenNewObject_whenCreate_ThenDefaultAlgorithmIsSHA256(){
        // When:
        HashGenerator generator = new HashGenerator();
        // Then:
        assertThat(generator.getAlgorithm()).isNotNull();
        assertThat(generator.getAlgorithm()).isEqualTo("SHA-256");
    }

    @Test
    public void givenNewObject_whenCreate_ThenDefaultCharsetIsUTF8(){
        // When:
        HashGenerator generator = new HashGenerator();
        // Then:
        assertThat(generator.getCharsetName()).isNotNull();
        assertThat(generator.getCharsetName()).isEqualTo("UTF-8");
    }

    @ParameterizedTest
    @MethodSource("invalidConfig")
    public void givenInvalidConfig_whenHashPassword_thenExceptionIsThrown(String charset, String algorithm, String input){
        // Given:
        HashGenerator generator = new HashGenerator();
        generator.setAlgorithm(algorithm);
        generator.setCharsetName(charset);
        // When:
        Throwable throwable = catchThrowable(() -> generator.hashPassword(input));
        // Then:
        assertTrue(throwable instanceof HashGenerationException);
    }

    @ParameterizedTest
    @MethodSource("validArgumentsForSHA256")
    public void givenValidString_whenHashWithSHA256_thenValidResult(String input, String output){
        assertThat(hashGenerator.hashPassword(input)).isEqualTo(output);
    }

    @ParameterizedTest
    @MethodSource("invalidArgumentsForSHA256")
    public void givenInvalidString_whenHashWithSHA256_thenValidResult(String input/*, String output*/){
        try {
            hashGenerator.hashPassword(input);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {}
    }

    private static Stream<Arguments> validArgumentsForSHA256(){
        return Stream.of(
                Arguments.of("123qweasdzxc","C8C38B9C87345D738153D778B33EC42BF13C5CD2D09F7C51B5F79C95F8A6D7F9"),
                Arguments.of("123qweasd", "85FD7C889F71CF105375595CDDC06B9D38FC562CB69C54F8C165AA751D81B3D9")
        );
    }

    private static Stream<Arguments> invalidArgumentsForSHA256(){
        return Stream.of(
                Arguments.of("",""),
                Arguments.of(null, "")
        );
    }

    private static Stream<Arguments> invalidConfig(){
        String invalidCharset = "invalid_charset";
        String invalidAlgorithm = "invalid_algorithm";
        String defaultCharset = new HashGenerator().getCharsetName();
        String defaultAlgorithm = new HashGenerator().getAlgorithm();
        String input = "123qweasdzxc";
        return Stream.of(
                Arguments.of(invalidCharset, invalidAlgorithm, input),
                Arguments.of(invalidCharset, defaultAlgorithm, input),
                Arguments.of(defaultCharset, invalidAlgorithm, input)
        );
    }
}
