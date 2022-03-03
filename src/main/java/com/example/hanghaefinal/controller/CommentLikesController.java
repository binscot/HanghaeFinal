package com.example.hanghaefinal.controller;


import com.example.hanghaefinal.dto.responseDto.CommentLikesResponseDto;
import com.example.hanghaefinal.security.UserDetailsImpl;
import com.example.hanghaefinal.service.CommentLikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentLikesController {

    private final CommentLikesService commentLikesService;

    @PostMapping("/commentLike/{commentId}")
    public CommentLikesResponseDto commentLike(@PathVariable Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return commentLikesService.addCommentLike(commentId, userDetails.getUser().getId());
    }

}
