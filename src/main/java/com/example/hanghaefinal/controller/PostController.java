package com.example.hanghaefinal.controller;

import com.example.hanghaefinal.dto.requestDto.PostRequestDto;
import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.security.UserDetailsImpl;
import com.example.hanghaefinal.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/posts")
    public Boolean savePost(@RequestPart(value = "file") MultipartFile multipartFile,
                            @RequestPart(value = "data") PostRequestDto postRequestDto,
                            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        if(userDetails != null){
            User user = userDetails.getUser();
            postService.uploadImageFile(multipartFile, postRequestDto);
            postService.savePost(postRequestDto ,user);
        } else throw new IllegalArgumentException("로그인한 유저 정보가 없습니다.");

        return true;
    }
}
