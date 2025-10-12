package com.example.assignment_4.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentCreateRequest {
    @NotBlank(message = "content_required")
    private String content;
}
