package com.example.hanghaefinal.dto.requestDto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class NoticeRequestDto {
    private String title;
    private String content;
    private MultipartFile noticeImg;
}
