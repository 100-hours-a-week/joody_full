package com.example.assignment_4.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 모든 API 응답의 공통 포맷
 * {
 *   "message": "login_success",
 *   "data": { ... }
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder().message("ok").data(data).build();
    }

    public static ApiResponse<Void> msg(String message) {
        return ApiResponse.<Void>builder().message(message).build();
    }
}
