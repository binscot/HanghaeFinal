package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.PostLikes;
import lombok.Getter;

@Getter
public class PostLikeClickersResponseDto {
    private Long userKey;

    // 각 ParagraphLikes 의
    public PostLikeClickersResponseDto(PostLikes postLikes) {
        this.userKey = postLikes.getUser().getId();
    }
}