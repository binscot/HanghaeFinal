package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.ParagraphLikes;
import lombok.Getter;

@Getter
public class ParagraphLikesClickUserKeyResDto {
    private Long paragraphLikesClickUserKey;

    // 각 ParagraphLikes 의
    public ParagraphLikesClickUserKeyResDto(ParagraphLikes paragraphLikes){
        this.paragraphLikesClickUserKey = paragraphLikes.getUser().getId();
    }
}
