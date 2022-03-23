package com.example.hanghaefinal.dto.responseDto;


import com.example.hanghaefinal.model.Post;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookmarkGetResponseDto {
    private final Long bookmarkKey;
    private final Long postKey;
    private final Post post;
    private final Long userId;
    private List<CategoryResponseDto> categoryResponseDtoList;

    public BookmarkGetResponseDto(Long id, Post post, Long userId, List<CategoryResponseDto> categoryResponseDtoList){
        this.bookmarkKey = id;
        this.postKey = post.getId();
        this.post = post;
        this.userId = userId;
        this.categoryResponseDtoList = categoryResponseDtoList;
    }
}
