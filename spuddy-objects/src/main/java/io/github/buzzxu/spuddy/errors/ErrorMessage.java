package io.github.buzzxu.spuddy.errors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by xux on 2017/4/23.
 */
@NoArgsConstructor
@Getter
public class ErrorMessage {

    private String message;
    private String requestUri;
    private String code;
    private int status;

    @JsonCreator
    public ErrorMessage(@JsonProperty("code") String code,
                        @JsonProperty("status") int status,
                        @JsonProperty("requestUri") String requestUri,
                        @JsonProperty(value = "message", defaultValue = "") String message) {
        this.status = status;
        this.code = code;
        this.requestUri = requestUri;
        this.message = message;
    }
}
