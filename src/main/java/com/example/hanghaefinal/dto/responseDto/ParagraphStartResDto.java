package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.dto.requestDto.ParagraphStartReqDto;
import com.example.hanghaefinal.security.UserDetailsImpl;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ParagraphStartResDto {
    private String username;
    private String nikcname;
    private Long postId;
    private String resMessage;
    private LocalDateTime createdAt;

    public ParagraphStartResDto(ParagraphStartReqDto paragraphStartReqDto){
        /*this.username = userDetails.getUsername();
        this.nikcname = userDetails.getUser().getNickName();
        this.postId = paragraphStartReqDto.getPostId();
        this.resMessage = userDetails.getUser().getNickName() + "님이 글을 작성 중입니다.";*/

        this.username = paragraphStartReqDto.getUsername();
        this.postId = paragraphStartReqDto.getPostId();
        this.resMessage = paragraphStartReqDto.getUsername() + "님이 글을 작성 중입니다.";
        this.createdAt = LocalDateTime.now();
    }
}
