package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.CommentLikes;
import lombok.Getter;

@Getter
public class CommentLikeClickersResponseDto {
    private Long userKey;

    // 각 ParagraphLikes 의
    public CommentLikeClickersResponseDto(CommentLikes commentLikes) {
        this.userKey = commentLikes.getUser().getId();
    }
}

