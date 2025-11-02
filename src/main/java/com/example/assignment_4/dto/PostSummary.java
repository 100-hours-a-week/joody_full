package com.example.assignment_4.dto;

import com.example.assignment_4.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 요약 정보 DTO")
public class PostSummary {

    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "게시글 제목", example = "오늘의 개발 일지")
    private String title;

    @Schema(description = "작성자 닉네임", example = "joody")
    private String author;

    @Schema(description = "조회수", example = "105")
    private int views;

    @Schema(description = "좋아요 수", example = "8")
    private int likes;

    @Schema(description = "댓글 수", example = "3")
    private int commentCount;

    @Schema(description = "작성일시", example = "2025-10-19T12:45:00")
    private String createdAt;

    @Schema(description = "게시글 본문 내용", example = "오늘은 Swagger UI를 적용했다.")
    private String content;

    /**
     * ✅ Entity → DTO 변환용 정적 메서드
     * Post 엔티티를 PostSummary DTO로 변환한다.
     */
    public static PostSummary from(Post post) {
        return new PostSummary(
                post.getId(),
                post.getTitle(),
                post.getUser() != null ? post.getUser().getNickname() : "탈퇴한 사용자",
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getCreatedAt() != null ? post.getCreatedAt().toString() : null,
                post.getContent()
        );
    }
}
