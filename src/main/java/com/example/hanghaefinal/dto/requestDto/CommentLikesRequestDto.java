package com.example.hanghaefinal.dto.requestDto;

import com.example.hanghaefinal.model.Comment;
import com.example.hanghaefinal.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CommentLikesRequestDto {
    private User user;
    private Comment comment;
}
