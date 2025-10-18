package com.example.assignment_4.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Schema(description = "게시글 작성 및 수정 요청 DTO")
public class PostRequest {

    @Schema(description = "게시글 제목", example = "오늘의 일기", maxLength = 26)
    @NotBlank(message = "title_required")
    @Size(max = 26, message = "title_too_long")
    private String title;

    @Schema(description = "게시글 내용", example = "오늘은 Spring Boot와 Swagger를 연결했다.")
    @NotBlank(message = "content_required")
    private String content;

    @Schema(description = "첨부 이미지 파일", example = "image.png")
    private MultipartFile image;
}
