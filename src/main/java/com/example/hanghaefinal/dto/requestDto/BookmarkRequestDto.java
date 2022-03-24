package com.example.hanghaefinal.dto.requestDto;

import com.example.hanghaefinal.model.Post;
import com.example.hanghaefinal.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class BookmarkRequestDto {

    private User user;
    private Post post;
}