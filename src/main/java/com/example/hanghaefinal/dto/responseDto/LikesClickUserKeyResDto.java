package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.ParagraphLikes;
import com.example.hanghaefinal.model.PostLikes;
import lombok.Getter;

@Getter
public class LikesClickUserKeyResDto {
    private Long userKey;

    public LikesClickUserKeyResDto(ParagraphLikes paragraphLikes){
        this.userKey = paragraphLikes.getUser().getId();
    }

    public LikesClickUserKeyResDto(PostLikes postLikes){
        this.userKey = postLikes.getUser().getId();
    }
}
