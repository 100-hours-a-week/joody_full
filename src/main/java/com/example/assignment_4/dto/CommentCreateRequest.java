package com.example.assignment_4.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "댓글 작성 요청 DTO")
public class CommentCreateRequest {

    @Schema(description = "댓글 내용", example = "좋은 글이네요! 감사합니다!")
    @NotBlank(message = "content_required")
    private String content;
}
