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

    @Schema(description = "이메일", example = "joody@example.com") 
    private String email;

    @Schema(description = "닉네임", example = "joody")
    private String nickname;


    @Schema(description = "프로필 이미지 URL", example = "http://localhost:8080/images/profile/1.png")
    private String profileImage;  //
}
