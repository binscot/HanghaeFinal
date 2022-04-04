package com.example.hanghaefinal.controller;

import com.example.hanghaefinal.model.Comment;
import com.example.hanghaefinal.dto.requestDto.CommentRequestDto;
import com.example.hanghaefinal.service.CommentService;
import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //코멘트 조회
    @GetMapping("/comment/{postId}")
    public List<Comment> getComment(@PathVariable Long postId){
        return commentService.getComment(postId);
    }

    //코멘트 작성
    @PostMapping("/comment/{postId}")
    public Comment addComment(@PathVariable Long postId, @Validated @RequestBody CommentRequestDto commentRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        return commentService.addComment(postId, commentRequestDto,user);
    }


    //코멘트 수정
    @PutMapping("/comment/{commentId}")
    public Comment update(@PathVariable Long commentId, @RequestBody CommentRequestDto commentRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return commentService.update(commentId,commentRequestDto,userDetails);
    }


    //코멘트 삭제
    @DeleteMapping("/comment/{commentId}")
    public void deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        commentService.deleteComment(commentId, userDetails);
    }
}

