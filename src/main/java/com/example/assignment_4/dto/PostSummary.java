package com.example.assignment_4.dto;

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
}
