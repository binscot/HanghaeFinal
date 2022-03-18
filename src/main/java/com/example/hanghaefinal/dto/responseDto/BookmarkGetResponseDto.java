package com.example.hanghaefinal.dto.responseDto;


import com.example.hanghaefinal.model.Post;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookmarkGetResponseDto {
    private final Long id;
    private final Post post;
    private final Long userId;

    public BookmarkGetResponseDto(Long id, Post post, Long userId){
        this.id = id;
        this.post = post;
        this.userId = userId;
    }
}
