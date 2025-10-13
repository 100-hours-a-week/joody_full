package com.example.assignment_4.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    private Long id;  // ✅ 회원 고유 ID 추가

    @NotBlank(message = "email_required")
    @Email(message = "invalid_email_format")
    private String email;

    @NotBlank(message = "password_required")
    @Size(min = 8, message = "password_min_8")
    private String password;

    @NotBlank(message = "password_check_required")
    private String password_check;

    @NotBlank(message = "nickname_required")
    @Size(min = 1, max = 10, message = "nickname_length_1_10")
    private String nickname;

    private String profile_image;
}
