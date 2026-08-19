package com.myproject.gajian.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

public record ApiResponse<T>(
        boolean success,
        String message,
        @JsonInclude(JsonInclude.Include.ALWAYS) T data,
        @JsonInclude(JsonInclude.Include.ALWAYS) Object meta) {

    public static <T> ApiResponse<T> of(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }
}
