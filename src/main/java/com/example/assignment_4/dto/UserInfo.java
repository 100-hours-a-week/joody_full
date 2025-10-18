package com.example.assignment_4.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "사용자 기본 정보 DTO")
public class UserInfo {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "닉네임", example = "joody")
    private String nickname;
}
