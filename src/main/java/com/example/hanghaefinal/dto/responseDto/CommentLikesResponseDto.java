package com.example.hanghaefinal.dto.responseDto;

import lombok.Getter;

@Getter
public class CommentLikesResponseDto {
    private Long commentId;
    private Long commentTotalLike;

    public CommentLikesResponseDto(Long commentId, Long commentTotalLike) {
        this.commentId = commentId;
        this.commentTotalLike = commentTotalLike;
    }
}
