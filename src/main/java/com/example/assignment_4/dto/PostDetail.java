package com.example.assignment_4.dto;

import com.example.assignment_4.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 상세 조회 응답 DTO")
public class PostDetail {

    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "게시글 제목", example = "오늘의 개발 일지")
    private String title;

    @Schema(description = "작성자 닉네임", example = "joody")
    private String author;

    @Schema(description = "작성자 프로필 이미지 URL")
    private String authorProfileImage;  // ✅ 추가

    @Schema(description = "작성일시", example = "2025-10-19T15:30:00")
    private String createdAt;

    @Schema(description = "게시글 내용", example = "오늘은 Swagger를 적용해봤다.")
    private String content;

    @Schema(description = "게시글 이미지 URL", example = "http://localhost:8080/uploads/example.png")
    private String postImage;  // ✅ 추가된 필드!

    @Schema(description = "조회수", example = "123")
    private int views;

    @Schema(description = "좋아요 수", example = "10")
    private int likes;


    @Schema(description = "댓글 수", example = "5")
    private int commentCount;

    @Schema(description = "댓글 목록", implementation = CommentSummary.class)
    private List<CommentSummary> comments;

    public static PostDetail from(Post post) {
        return new PostDetail(
                post.getId(),
                post.getTitle(),
                post.getUser() != null ? post.getUser().getNickname() : "탈퇴한 사용자",
                post.getUser() != null ? post.getUser().getProfileImage() : null, // ✅ 추가된 부분!
                post.getCreatedAt() != null ? post.getCreatedAt().toString() : null,
                post.getContent(),
                post.getPostImage(), // ✅ 이미지 경로 매핑 추가
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                Collections.emptyList() // 댓글 리스트는 CommentService에서 별도 주입 예정

        );
    }
}
