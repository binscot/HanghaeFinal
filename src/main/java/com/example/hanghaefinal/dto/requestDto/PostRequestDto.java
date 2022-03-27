package com.example.hanghaefinal.dto.requestDto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Getter
@Validated
public class PostRequestDto {

    @NotBlank(message = "제목을 입력해 주세요!")
    @Size(min = 1, max = 100, message = "제목을 100자 이내로 입력해주세요")
    private String title;
    private MultipartFile postImageUrl;
    private String color;
    private int limitCnt;
    private String category;
    private boolean complete;

    @NotBlank(message = "문단을 입력해 주세요!")
    @Size(min = 1, max = 2000, message = "문단을 2000자 이내로 입력해주세요")
    private String paragraph;
}
