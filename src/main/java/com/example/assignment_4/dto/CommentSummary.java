package com.example.assignment_4.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글에 대한 댓글 요약 DTO")
public class CommentSummary {

    @Schema(description = "댓글 ID", example = "10")
    private Long commentId;

    @Schema(description = "댓글 작성자 닉네임", example = "joody")
    private String author;

    @Schema(description = "댓글 내용", example = "좋은 정보 감사합니다!")
    private String content;

    @Schema(description = "댓글 작성 일시", example = "2025-10-19T15:30:00")
    private String createdAt;
}
