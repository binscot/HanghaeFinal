package com.example.hanghaefinal.controller;

import com.example.hanghaefinal.dto.responseDto.PostLikesResponseDto;
import com.example.hanghaefinal.security.UserDetailsImpl;
import com.example.hanghaefinal.service.PostLikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostLikesController {

    private final PostLikesService postLikesService;

    @PostMapping("/postLikes/{postId}")
    public PostLikesResponseDto postLike(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return postLikesService.addLike(postId, userDetails.getUser().getId());
    }

}



