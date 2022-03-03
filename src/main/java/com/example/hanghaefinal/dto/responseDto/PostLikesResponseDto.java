package com.example.hanghaefinal.dto.responseDto;

import lombok.Getter;

@Getter
public class PostLikesResponseDto {
    private Long postId;
    private Long totalLike;

    public  PostLikesResponseDto(Long postId, Long totalLike){
        this.postId = postId;
        this.totalLike = totalLike;
    }
}
