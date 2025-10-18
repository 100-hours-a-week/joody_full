package com.example.assignment_4.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "댓글 응답 DTO")
public class CommentResponse {

    @Schema(description = "댓글 고유 ID", example = "1")
    private Long commentId;
}
