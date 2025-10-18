package com.example.assignment_4.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 요청 DTO")
public class SignupRequest {

    @Schema(description = "회원 ID (자동 생성)", example = "1")
    private Long id;

    @Schema(description = "회원 이메일", example = "example@example.com")
    @NotBlank(message = "email_required")
    @Email(message = "invalid_email_format")
    private String email;

    @Schema(description = "비밀번호", example = "P@ssw0rd!", minLength = 8)
    @NotBlank(message = "password_required")
    @Size(min = 8, message = "password_min_8")
    private String password;

    @Schema(description = "비밀번호 확인", example = "P@ssw0rd!")
    @NotBlank(message = "password_check_required")
    private String password_check;

    @Schema(description = "닉네임", example = "joody", minLength = 1, maxLength = 10)
    @NotBlank(message = "nickname_required")
    @Size(min = 1, max = 10, message = "nickname_length_1_10")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "http://localhost:8080/uploads/profile_1.png")
    private String profile_image;
}
