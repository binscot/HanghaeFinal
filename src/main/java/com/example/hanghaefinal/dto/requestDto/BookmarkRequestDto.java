package com.example.hanghaefinal.dto.requestDto;

import com.example.hanghaefinal.model.Post;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookmarkRequestDto {

    private Long id;
    private Long userId;
    private Post post;
}