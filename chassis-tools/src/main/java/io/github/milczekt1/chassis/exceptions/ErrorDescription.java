package io.github.milczekt1.chassis.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ErrorDescription {

    @JsonProperty("status")
    private Integer status;
    @JsonProperty("statusName")
    private String statusName;
    @JsonProperty("message")
    private String message;
    @JsonProperty("timestamp")
    private Instant timestamp;

    public ErrorDescription(HttpStatus httpStatus, String message) {
        setStatusName(httpStatus.name());
        setStatus(httpStatus.value());
        setMessage(message);
        setTimestamp(Instant.now());
    }
}
