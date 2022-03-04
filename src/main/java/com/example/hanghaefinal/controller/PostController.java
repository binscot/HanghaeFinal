package com.example.hanghaefinal.controller;

import com.example.hanghaefinal.dto.requestDto.PostRequestDto;
import com.example.hanghaefinal.dto.responseDto.OtherUserResDto;
import com.example.hanghaefinal.dto.responseDto.PostDetailResponseDto;
import com.example.hanghaefinal.dto.responseDto.PostResponseDto;
import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.security.UserDetailsImpl;
import com.example.hanghaefinal.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

//    @PostMapping("/posts")
//    public Boolean savePost(@RequestPart(value = "file") MultipartFile multipartFile,
//                            @RequestPart(value = "data") PostRequestDto postRequestDto,
//                            @AuthenticationPrincipal UserDetailsImpl userDetails
    @PostMapping("/posts")
    public Boolean savePost(@ModelAttribute PostRequestDto postRequestDto,
                            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        if(userDetails != null){
            User user = userDetails.getUser();
            String defaultImg = postService.uploadImageFile(postRequestDto.getPostImageUrl(), postRequestDto);
            //postService.uploadImageFile(multipartFile, postRequestDto);
            postService.savePost(postRequestDto ,user, defaultImg);
        } else throw new IllegalArgumentException("로그인한 유저 정보가 없습니다.");

        return true;
    }

    @GetMapping("/posts/{postId}")
    public PostDetailResponseDto viewPostDetail(@PathVariable Long postId){
        return postService.viewPostDetail(postId);
    }

    // 완성작 게시글 전체 조회 - 최신순
    @GetMapping("/posts/recent")
    public List<PostResponseDto> viewPostRecent(){
        return postService.viewPostRecent();
    }

    // 완성작 게시글 전체 조회 - 추천순(좋아요순)
    @GetMapping("/posts/recommend")
    public List<PostResponseDto> viewPostRecommend(){
        return postService.viewPostRecommend();
    }

    // 미완성 게시글 전체 조회 - 최신순
    @GetMapping("/posts/incomplete")
    public List<PostResponseDto> viewPostIncomplete(){
        return postService.viewPostIncomplete();
    }

    // 다른 유저 페이지
    @GetMapping("/posts/userPage/{userKey}")
    public OtherUserResDto viewUserPage(@PathVariable Long userKey){
        return postService.viewUserPage(userKey);
    }
}
