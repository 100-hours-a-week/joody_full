package com.example.assignment_4.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PostRequest {

    @NotBlank(message = "title_required")
    @Size(max = 26, message = "title_too_long")
    private String title;

    @NotBlank(message = "content_required")
    private String content;

    private MultipartFile image;  // 이미지 파일도 포함
}
