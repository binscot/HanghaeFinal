package com.example.hanghaefinal.service;


import com.example.hanghaefinal.exception.exception.*;
import com.example.hanghaefinal.model.Comment;
import com.example.hanghaefinal.repository.CommentRepository;
import com.example.hanghaefinal.dto.requestDto.CommentRequestDto;
import com.example.hanghaefinal.model.Post;
import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.repository.PostRepository;
import com.example.hanghaefinal.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    //코멘트 조회
    public List<Comment> getComment(Long postId){

        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }

    //코멘트 작성
    @Transactional
    public Comment addComment(Long postId, CommentRequestDto commentRequestDto, User user){
        Post post = postRepository.findById(postId).orElseThrow(
                ()-> new PostNotFoundException("postId가 존재하지 않습니다.")
        );

        if (commentRequestDto.getComment() == null){
            throw new ContentNullException("내용을 입력해주세요.");
        }
        if (commentRequestDto.getComment().length() > 200){
            throw new CommentLimitException("200자 이하로 작성해주세요.");
        }
        else{
            Comment comment = new Comment(commentRequestDto,post,user);
            return commentRepository.save(comment);
        }
    }

    //코멘트 수정
    @Transactional
    public Comment update(Long commentId, CommentRequestDto commentRequestDto, UserDetailsImpl userDetails) {

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CommentNotFoundException("해당 댓글이 존재하지 않습니다.")
        );

        User user = comment.getUser();
        if (userDetails.getUser() != user) {
            throw new IllegalUserException("해당 작성자가 아닙니다.");
        }
        if (commentRequestDto.getComment() == null) {
            throw new ContentNullException("댓글을 입력해주세요.");
        }
        if (commentRequestDto.getComment().length() > 200) {
            throw new CommentLimitException("댓글은 200자 이하로 작성해주세요!!");
        } else {
            comment.update(commentRequestDto);
            return comment;
        }
    }


    //코멘트 삭제
    @Transactional
    public void deleteComment(Long commentId, UserDetailsImpl userDetails){
       Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CommentNotFoundException("해당 댓글이 존재하지 않습니다.")
        );
        User user = comment.getUser();
        Long deleteId = user.getId();
        if(!Objects.equals(userDetails.getUser().getId(), deleteId)) {
            throw new IllegalUserException("해당 작성자가 아닙니다.");
        }else{
            commentRepository.deleteById(commentId);
        }


    }

}
