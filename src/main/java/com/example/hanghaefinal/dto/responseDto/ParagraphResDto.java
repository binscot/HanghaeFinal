package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.Paragraph;
import com.example.hanghaefinal.model.Post;
import com.example.hanghaefinal.model.User;
import lombok.Getter;

@Getter
public class ParagraphResDto {
    private Paragraph.MessageType type;
    private String paragraph;
    private Long postId;
    private UserInfoResponseDto userInfoResDto;

    public ParagraphResDto(Paragraph paragraph){
        this.paragraph = paragraph.getParagraph();
    }

    public ParagraphResDto(Paragraph paragraph, UserInfoResponseDto userInfoResponseDto){
        this.paragraph = paragraph.getParagraph();
        this.userInfoResDto = userInfoResponseDto;
    }


    public ParagraphResDto(Paragraph paragraph, Long postId, UserInfoResponseDto userInfoResDto){
        this.type = paragraph.getType();
        this.paragraph = paragraph.getParagraph();
        this.postId = postId;
        this.userInfoResDto = userInfoResDto;
    }

    /*public ParagraphResDto(Paragraph paragraph, Long postId, User user){
        this.type = paragraph.getType();
        this.paragraph = paragraph.getParagraph();
        this.postId = postId;
        this.userKey = user.getId();
        this.username = user.getUsername();
        this.nickName = user.getNickName();
        this.userProfileImage = user.getUserProfileImage();
    }*/
}
