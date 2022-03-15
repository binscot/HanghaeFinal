package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.PostLikes;
import lombok.Getter;

@Getter
public class PostLikeClickersResponseDto {
    private Long userId;

    // 각 ParagraphLikes 의
    public PostLikeClickersResponseDto(PostLikes postLikes) {
        this.userId = postLikes.getUser().getId();
    }
}