package pl.konradboniecki.chassis.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class ErrorDescription {

    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("statusName")
    private String statusName;
    @JsonProperty("message")
    private String message;

    public ErrorDescription(HttpStatus httpStatus, String message) {
        setStatusName(httpStatus.name());
        setStatus(httpStatus.value());
        setMessage(message);
        setTimestamp(LocalDateTime.now());
    }
}
