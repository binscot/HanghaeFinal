package com.example.hanghaefinal.dto.responseDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class NoticeResponseDto {
    private String title;
    private String content;
    private String noticeImg;
    private String noticeCreatedAt;
    private String noticeModifiedAt;


    public NoticeResponseDto(String title, String content, String noticeImg,LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.noticeCreatedAt= createdAt.toString();
        this.noticeModifiedAt=modifiedAt.toString();
        this.title=title;
        this.content=content;
        this.noticeImg=noticeImg;


    }

}
