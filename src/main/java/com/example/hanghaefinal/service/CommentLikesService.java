package com.example.hanghaefinal.service;

import com.example.hanghaefinal.dto.requestDto.CommentLikesRequestDto;
import com.example.hanghaefinal.dto.responseDto.CommentLikeClickersResponseDto;
import com.example.hanghaefinal.dto.responseDto.CommentLikesResponseDto;
import com.example.hanghaefinal.model.Comment;
import com.example.hanghaefinal.model.CommentLikes;
import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.repository.CommentLikesRepository;
import com.example.hanghaefinal.repository.CommentRepository;
import com.example.hanghaefinal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentLikesService {

    private final CommentLikesRepository commentLikesRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentLikesResponseDto addCommentLike(Long commentId, Long userId){
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("유저정보가 없습니다.")
        );

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 없습니다.")
        );

        CommentLikes findCommentLike = commentLikesRepository.findByUserAndComment(user, comment).orElse(null);

        if(findCommentLike == null){
            CommentLikesRequestDto commentLikesRequestDto = new CommentLikesRequestDto(user, comment);
            CommentLikes commentLikes = new CommentLikes(commentLikesRequestDto);
            commentLikesRepository.save(commentLikes);
        } else{
            commentLikesRepository.deleteById(findCommentLike.getId());
        }

        List<CommentLikes> commentLikes = commentLikesRepository.findAllByPostId(commentId);
        List<CommentLikeClickersResponseDto> commentLikeClickersResponseDtos = new ArrayList<>();
        for (CommentLikes commentLikesTemp : commentLikes) {
            commentLikeClickersResponseDtos.add(new CommentLikeClickersResponseDto(commentLikesTemp));
        }

        return new CommentLikesResponseDto(commentId, commentLikeClickersResponseDtos,commentLikesRepository.countByComment(comment));
    }
    }
