package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.dto.requestDto.ParagraphCompleteReqDto;
import com.example.hanghaefinal.security.UserDetailsImpl;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ParagraphCompleteResDto {
    private String username;
    private String nickname;
    private Long postId;
    private String paragraph;
    private LocalDateTime createdAt;

    public ParagraphCompleteResDto(ParagraphCompleteReqDto paragraphCompleteReqDto){
        /*this.username = userDetails.getUsername();
        this.nickname = userDetails.getUser().getNickName();
        this.postId = paragraphCompleteReqDto.getPostId();
        this.paragraph = paragraphCompleteReqDto.getParagraph();
        this.createdAt = LocalDateTime.now();*/
        this.username = paragraphCompleteReqDto.getUsername();
        this.postId = paragraphCompleteReqDto.getPostId();
        this.paragraph = paragraphCompleteReqDto.getParagraph();
        this.createdAt = LocalDateTime.now();
    }
}
