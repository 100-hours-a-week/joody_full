package com.example.assignment_4.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class LoginRequest {

    @Schema(description = "회원 이메일", example = "example@example.com")
    @NotBlank(message = "email_required")
    @Email(message = "invalid_email_format")
    private String email;

    @Schema(description = "비밀번호", example = "P@ssw0rd!")
    @NotBlank(message = "password_required")
    private String password;
}
