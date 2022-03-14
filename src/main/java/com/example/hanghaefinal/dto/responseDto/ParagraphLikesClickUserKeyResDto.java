package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.ParagraphLikes;
import lombok.Getter;

@Getter
public class ParagraphLikesClickUserKeyResDto {
    private Long userId;

    // 각 ParagraphLikes 의
    public ParagraphLikesClickUserKeyResDto(ParagraphLikes paragraphLikes){
        this.userId = paragraphLikes.getUser().getId();
    }
}
