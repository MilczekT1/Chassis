package pl.konradboniecki.chassis.tools;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static java.util.Collections.singletonList;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class RestTools {

    public static HttpHeaders defaultPostHTTPHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_JSON);
        httpHeaders.setAccept(singletonList(APPLICATION_JSON));
        return httpHeaders;
    }

    public static HttpHeaders defaultGetHTTPHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }
}
