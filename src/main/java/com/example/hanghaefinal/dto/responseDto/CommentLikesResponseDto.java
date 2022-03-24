package com.example.hanghaefinal.dto.responseDto;

import lombok.Getter;

import java.util.List;

@Getter
public class CommentLikesResponseDto {
    private Long commentId;
    private List<CommentLikeClickersResponseDto> commentLikesClickersResponseDtos;
    private Long commentTotalLike;

    public CommentLikesResponseDto(Long commentId, List<CommentLikeClickersResponseDto> commentLikesClickersResponseDtos, Long commentTotalLike) {
        this.commentId = commentId;
        this.commentLikesClickersResponseDtos =commentLikesClickersResponseDtos;
        this.commentTotalLike = commentTotalLike;
    }
}
