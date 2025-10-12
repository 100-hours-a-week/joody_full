package com.example.assignment_4.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "email_required")
    @Email(message = "invalid_email_format")
    private String email;

    @NotBlank(message = "password_required")
    private String password;
}
