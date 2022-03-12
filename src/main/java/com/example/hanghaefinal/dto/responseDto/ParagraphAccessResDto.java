package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.dto.requestDto.ParagraphReqDto;
import com.example.hanghaefinal.model.Paragraph;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParagraphAccessResDto {
    private Paragraph.MessageType type;
    private String postId;
    private String paragraph;
    private LocalDateTime time;
    private UserInfoResponseDto userInfoResponseDto;

    public ParagraphAccessResDto(ParagraphReqDto paragraphReqDto) {
        this.type = paragraphReqDto.getType();
        this.postId = paragraphReqDto.getPostId();
        this.paragraph = paragraphReqDto.getParagraph();
    }

    public ParagraphAccessResDto(ParagraphReqDto paragraphReqDto, UserInfoResponseDto userInfoResponseDto){
        this.type = paragraphReqDto.getType();
        this.postId = paragraphReqDto.getPostId();
        this.paragraph = paragraphReqDto.getParagraph();
        this.time = LocalDateTime.now();
        this.userInfoResponseDto = userInfoResponseDto;
    }

}
