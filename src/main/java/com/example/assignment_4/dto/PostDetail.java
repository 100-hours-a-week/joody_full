package com.example.assignment_4.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDetail {
    private Long postId;
    private String title;
    private String author;
    private String createdAt;
    private String content;
    private int views;
    private int likes;
    private int commentCount;
    private List<CommentSummary> comments;
}
