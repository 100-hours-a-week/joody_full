package com.example.assignment_4.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostSummary {
    private Long postId;
    private String title;
    private String author;
    private int views;
    private int likes;
    private int commentCount;
    private String createdAt;
    private String content;
}
