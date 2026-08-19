package com.myproject.gajian.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        boolean success,
        String message,
        @JsonProperty("error_code") String errorCode,
        List<ValidationError> errors,
        @JsonInclude(JsonInclude.Include.ALWAYS) Object data) {

    public static ApiErrorResponse of(String message, String errorCode) {
        return new ApiErrorResponse(false, message, errorCode, null, null);
    }

    public static ApiErrorResponse validation(String message, List<ValidationError> errors) {
        return new ApiErrorResponse(false, message, null, errors, null);
    }

    public record ValidationError(String field, String message) {
    }
}
