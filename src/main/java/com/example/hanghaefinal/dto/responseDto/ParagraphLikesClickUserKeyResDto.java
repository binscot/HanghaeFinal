package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.Paragraph;
import com.example.hanghaefinal.model.ParagraphLikes;
import lombok.Getter;

@Getter
public class ParagraphLikesClickUserKeyResDto {
    //private Long paragraphLikesClickUserKey;
    private Long userKey;

    // 각 ParagraphLikes 의
    public ParagraphLikesClickUserKeyResDto(ParagraphLikes paragraphLikes){
        //this.paragraphLikesClickUserKey = paragraphLikes.getUser().getId();
        this.userKey = paragraphLikes.getUser().getId();
    }
}
