package com.example.assignment_4.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Schema(description = "작성일시", example = "2025-10-19T15:30:00")
    private String createdAt;

    @Schema(description = "게시글 내용", example = "오늘은 Swagger를 적용해봤다.")
    private String content;

    @Schema(description = "조회수", example = "123")
    private int views;

    @Schema(description = "좋아요 수", example = "10")
    private int likes;

    @Schema(description = "댓글 수", example = "5")
    private int commentCount;

    @Schema(description = "댓글 목록", implementation = CommentSummary.class)
    private List<CommentSummary> comments;
}
