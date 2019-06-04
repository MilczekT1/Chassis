package pl.konradboniecki.chassis;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorDescriptionTests {

    @Test
    public void test_error_description_creation(){
        String errorMessage = "Something has not been found";
        ErrorDescription errorDesc = new ErrorDescription(HttpStatus.NOT_FOUND, errorMessage);
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value() , errorDesc.getStatus().longValue()),
                () -> assertEquals("NOT_FOUND", errorDesc.getStatusName()),
                () -> assertEquals(errorMessage, errorDesc.getMessage()),
                () -> assertTrue(errorDesc.getTimestamp().isBefore(LocalDateTime.now()))
        );
    }
}
