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

    public ParagraphResDto(Paragraph paragraph, Long postId, UserInfoResponseDto userInfoResDto){
        this.paragraph = paragraph.getParagraph();
        this.postId = postId;
        this.userInfoResDto = userInfoResDto;
    }
}
