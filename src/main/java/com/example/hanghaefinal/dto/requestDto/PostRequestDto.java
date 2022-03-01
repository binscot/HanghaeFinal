package com.example.hanghaefinal.dto.requestDto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PostRequestDto {
    private String title;
    private String imageUrl;
    private String color;
    private int limitCnt;
}
