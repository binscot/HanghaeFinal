package com.example.hanghaefinal.dto.responseDto;

import lombok.Getter;

import java.util.List;

@Getter
public class PostLikesResponseDto {
    private Long postId;
    private List<PostLikeClickersResponseDto> postLikeClickersResponseDtos;
    private Long totalLike;

    public  PostLikesResponseDto(Long postId, List<PostLikeClickersResponseDto> postLikeClickersResponseDtos, Long totalLike){
        this.postId = postId;
        this.postLikeClickersResponseDtos = postLikeClickersResponseDtos;
        this.totalLike = totalLike;
    }

}
