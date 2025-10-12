package com.example.assignment_4.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentSummary {
    private Long commentId;
    private String author;
    private String content;
    private String createdAt;
}
