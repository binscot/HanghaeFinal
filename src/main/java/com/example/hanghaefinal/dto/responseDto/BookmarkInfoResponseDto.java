package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.Post;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookmarkInfoResponseDto {
    private final Long id;
    private final Long postId;
    private final Long userId;

    public BookmarkInfoResponseDto(Long id, Long postId, Long userId){
        this.id = id;
        this.postId = postId;
        this.userId = userId;
    }
}


