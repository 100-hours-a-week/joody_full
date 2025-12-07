package com.example.assignment_4.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "댓글 수정 요청 DTO")
public class CommentUpdateRequest {

    @Schema(description = "수정할 댓글 내용", example = "내용을 수정했습니다.")
    @NotBlank(message = "content_required")
    private String content;
}
