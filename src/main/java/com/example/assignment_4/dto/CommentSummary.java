package com.example.assignment_4.dto;

import com.example.assignment_4.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "댓글 요약 DTO")
public class CommentSummary {

    @Schema(description = "댓글 ID", example = "1")
    private Long id;

    @Schema(description = "작성자 닉네임", example = "joody")
    private String author;

    @Schema(description = "댓글 내용", example = "좋은 글이에요!")
    private String content;

    @Schema(description = "작성일", example = "2025-11-03T12:30:00")
    private String createdAt;

    public static CommentSummary from(Comment comment) {
        return CommentSummary.builder()
                .id(comment.getId())
                .author(comment.getUser().getNickname())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt().toString())
                .build();
    }
}
