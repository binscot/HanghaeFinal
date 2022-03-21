package com.example.hanghaefinal.dto.responseDto;

import lombok.Getter;

import java.util.List;

@Getter
public class ParagraphLikesResDto {
    private Long paragraphKey;
    private List<ParagraphLikesClickUserKeyResDto> paragraphLikesClickUserKeyResDtoList;
    private Long paragraphTotalLikes;

    public ParagraphLikesResDto(Long paragraphId, List<ParagraphLikesClickUserKeyResDto> paragraphLikesClickUserKeyResDtoList ,Long paragraphTotalLikes){
        this.paragraphKey = paragraphId;
        this.paragraphLikesClickUserKeyResDtoList = paragraphLikesClickUserKeyResDtoList;
        this.paragraphTotalLikes = paragraphTotalLikes;
    }

}