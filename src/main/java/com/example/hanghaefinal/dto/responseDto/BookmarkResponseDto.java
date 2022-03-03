package com.example.hanghaefinal.dto.responseDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookmarkResponseDto {
    private final Long id;
    private final Long postId;
    private final Long userId;

    public BookmarkResponseDto(Long id, Long postId, Long userId){
        this.id = id;
        this.postId = postId;
        this.userId = userId;
    }
}

