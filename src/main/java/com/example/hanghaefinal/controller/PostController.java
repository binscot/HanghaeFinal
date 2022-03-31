package com.example.hanghaefinal.controller;

import com.example.hanghaefinal.dto.requestDto.CategoryRequestDto;
import com.example.hanghaefinal.dto.requestDto.PostRequestDto;
import com.example.hanghaefinal.dto.responseDto.OtherUserResDto2;
import com.example.hanghaefinal.dto.responseDto.PostDetailResponseDto;
import com.example.hanghaefinal.dto.responseDto.PostResponseDto;
import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.security.UserDetailsImpl;
import com.example.hanghaefinal.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 미완성 게시글 생성 요청
    @PostMapping("/posts")
    public ResponseEntity<Boolean> savePost(
            @Validated
            @ModelAttribute PostRequestDto postRequestDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        if(userDetails != null){
            User user = userDetails.getUser();
            String defaultImg = postService.uploadImageFile(postRequestDto.getPostImageUrl(), postRequestDto);
            //postService.uploadImageFile(multipartFile, postRequestDto);
            //postService.savePost(postRequestDto ,user, defaultImg, bindingResult);
            return ResponseEntity.ok(postService.savePost(postRequestDto ,user, defaultImg, bindingResult));
        } else throw new IllegalArgumentException("로그인한 유저 정보가 없습니다.");
        //return true; // postId로 return 할지 고려하자
    }

    // 미완성 게시글을 -> 완성 게시글로 변경 ( complete 컴럼만 수정할 것이므로 patch 사용)
    // 최초 게시글 생성자와 미완성을 완성으로 바꾸는 사용자가 같을 필요는 없고
    // 마지막 문단을 단 사람과 게시글 완성 버튼을 누른 사람이 일치하면 완성 되도록???
    @PatchMapping("/posts/complete/{postId}")
    public PostDetailResponseDto completePost(@PathVariable Long postId,
                                              @RequestBody CategoryRequestDto categoryRequestDto,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails){
        log.info(" ------ testcode1 ------ ");
        return postService.completePost(postId, categoryRequestDto, userDetails);
    }

    // 게시글 상세 페이지 조회 (완성/미완성)
    @GetMapping("/posts/{postId}")
    public PostDetailResponseDto viewPostDetail(@PathVariable Long postId){
        return postService.viewPostDetail(postId);
    }

    // 완성작 게시글 전체 조회 - 최신순
    @GetMapping("/posts/recent")
    public List<PostResponseDto> viewPostRecent(
            @RequestParam int page,
            @RequestParam int size){
        return postService.viewPostRecent(page, size);
    }

    // 완성작 게시글 전체 조회 - 추천순(좋아요순)
    @GetMapping("/posts/recommend")
    public List<PostResponseDto> viewPostRecommend(
            @RequestParam int page,
            @RequestParam int size
    ){
        return postService.viewPostRecommend(page, size);
    }

    // 미완성 게시글 전체 조회 - 최신순
    @GetMapping("/posts/incomplete")
    public List<PostResponseDto> viewPostIncomplete(
            @RequestParam int page,
            @RequestParam int size
    ){
        return postService.viewPostIncomplete(page, size);
    }

    // 메인에 보여줄 즐겨찾기 많은 순 & 댓글 있고 & 완성작 top3
    @GetMapping("/posts/viewMain")
    public List<PostResponseDto> viewPostMain(){
        return postService.viewPostMain();
    }

    // 사용자가(내가) 좋아요한 작품
    @GetMapping("/posts/viewMyLikesPost")
    public List<PostResponseDto> viewMyLikesPost(
            @RequestParam int page,
            @RequestParam int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        if(userDetails !=null ){
            return postService.viewMyLikesPost(page, size, userDetails.getUser());
        } else throw new IllegalArgumentException("로그인한 유저 정보가 없습니다.");
    }

    // 다른 유저 페이지
    @GetMapping("/posts/userPage/{userKey}")
    public OtherUserResDto2 viewUserPage2(
            @PathVariable Long userKey,
            @RequestParam int page,
            @RequestParam int size
    ) {
        return postService.viewUserParticipatePost(userKey, page, size);
    }

    // 다른 유저 페이지
/*    @GetMapping("/posts/userPage/{userKey}")
    public OtherUserResDto viewUserPage(
            @PathVariable Long userKey,
            @RequestParam int page,
            @RequestParam int size){
        return postService.viewUserPage(userKey,page, size);
    }*/
}