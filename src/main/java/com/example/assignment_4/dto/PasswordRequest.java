package com.example.assignment_4.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "비밀번호 변경 요청 DTO")
public class PasswordRequest {

    @Schema(description = "새 비밀번호", example = "SecureP@ssw0rd!", minLength = 8, maxLength = 20)
    @NotBlank(message = "newPassword_required")
    @Size(min = 8, max = 20, message = "password_length_8_20")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$",
            message = "password_invalid_format"
    )
    private String newPassword;

    @Schema(description = "비밀번호 확인", example = "SecureP@ssw0rd!")
    @NotBlank(message = "newPassword_check_required")
    private String newPassword_check;
}
