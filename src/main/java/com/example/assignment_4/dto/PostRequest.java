package com.example.assignment_4.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequest {

    @NotBlank(message = "title_required")
    @Size(max = 26, message = "title_too_long")
    private String title;

    @NotBlank(message = "content_required")
    private String content;
}


/* 여기서는 MultipartFile을 DTO에 직접 넣지 않고, Controller에서 따로 받는 방식으로 구현함.
이게 multipart/form-data 처리 시 가장 안정적이고 에러가 적음 */