package com.example.assignment_4.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "로그인 응답 DTO")
public class LoginResponse {

    @Schema(description = "로그인한 사용자 정보")
    private UserInfo user;

    @Schema(description = "JWT 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
}
