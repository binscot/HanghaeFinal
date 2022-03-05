package com.example.hanghaefinal.dto.requestDto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class PostRequestDto {
    private String title;
    private MultipartFile postImageUrl;
    private String color;
    private int limitCnt;
    private String category;
    private boolean complete;
}
