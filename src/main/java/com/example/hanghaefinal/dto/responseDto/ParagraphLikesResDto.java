package com.example.hanghaefinal.dto.responseDto;

import lombok.Getter;

@Getter
public class ParagraphLikesResDto {
    private Long paragraphKey;
    private Long paragraphTotalLikes;

    public ParagraphLikesResDto(Long paragraphId, Long paragraphTotalLikes){
        this.paragraphKey = paragraphId;
        this.paragraphTotalLikes = paragraphTotalLikes;
    }

}