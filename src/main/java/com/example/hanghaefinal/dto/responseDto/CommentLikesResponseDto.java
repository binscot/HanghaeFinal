package com.example.hanghaefinal.dto.responseDto;

import lombok.Getter;

@Getter
public class CommentLikesResponseDto {
    private Long commentId;
    private Long totalLike;

    public CommentLikesResponseDto(Long commentId, Long totalLike) {
        this.commentId = commentId;
        this.totalLike = totalLike;
    }
}
