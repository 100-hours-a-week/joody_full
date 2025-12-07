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

    @Schema(description = "작성자 ID", example = "24")
    private Long authorId;   // ⭐ 추가된 필드

    @Schema(description = "작성자 닉네임", example = "joody")
    private String author;

    @Schema(description = "작성자 프로필 이미지 URL")
    private String authorProfileImage;

    @Schema(description = "댓글 내용", example = "좋은 글이에요!")
    private String content;

    @Schema(description = "작성일", example = "2025-11-03T12:30:00")
    private String createdAt;

    @Schema(description = "수정일", example = "2025-11-03T14:10:00")
    private String updatedAt;

    public static CommentSummary from(Comment comment) {
        return CommentSummary.builder()
                .id(comment.getId())
                .authorId(comment.getUser().getId())
                .author(comment.getUser().getNickname())
                .authorProfileImage(comment.getUser().getProfileImage()) // ✅ 추가!
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt().toString())
                .updatedAt(
                        comment.getUpdatedAt() != null
                                ? comment.getUpdatedAt().toString()
                                : null
                )
                .build();
    }
}
