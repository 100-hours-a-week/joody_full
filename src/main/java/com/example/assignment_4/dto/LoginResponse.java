package com.example.assignment_4.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private UserInfo user;
    private String token;
}
