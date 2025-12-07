package com.example.assignment_4.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 닉네임 수정 요청 DTO")
public class ProfileUpdateRequest {

    @Schema(description = "새 닉네임", example = "joody_new", minLength = 1, maxLength = 10)
    @NotBlank(message = "nickname_required")
    @Size(min = 1, max = 10, message = "nickname_length_1_10")
    private String nickname;
}
