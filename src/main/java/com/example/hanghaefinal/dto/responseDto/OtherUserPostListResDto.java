package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.Post;
import lombok.Getter;

@Getter
public class OtherUserPostListResDto {
    private Long postKey;
    private String title;
    private String postImageUrl;
    private String color;
    private int limitCnt;
    private boolean complete;
    private String postCreatedAt;
    private String postModifiedAt;

    public OtherUserPostListResDto(Post post){
        this.postKey = post.getId();
        this.title = post.getTitle();
        this.postImageUrl = post.getPostImageUrl();
        this.color = post.getColor();
        this.limitCnt = post.getLimitCnt();
        this.complete = post.isComplete();
        this.postCreatedAt = post.getCreatedAt().toString();
        this.postModifiedAt = post.getModifiedAt().toString();
    }
}
