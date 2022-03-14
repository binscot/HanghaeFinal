package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.CommentLikes;
import lombok.Getter;

@Getter
public class CommentLikesClickersResponseDto {
    private Long userId;

    // 각 ParagraphLikes 의
    public CommentLikesClickersResponseDto(CommentLikes commentLikes) {
        this.userId = commentLikes.getUser().getId();
    }
}

