package com.example.hanghaefinal.service;

import com.example.hanghaefinal.dto.requestDto.PostLikesRequestDto;
import com.example.hanghaefinal.dto.responseDto.PostLikesResponseDto;
import com.example.hanghaefinal.model.Post;
import com.example.hanghaefinal.model.PostLikes;
import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.repository.PostLikesRepository;
import com.example.hanghaefinal.repository.PostRepository;
import com.example.hanghaefinal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostLikesService {

    private final PostLikesRepository postLikesRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final AlarmService alarmService;

    //좋아요 등록
    @Transactional
    public PostLikesResponseDto addLike(Long postId, Long userId){
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("유저정보가 없습니다.")
        );

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 없습니다.")
        );

        PostLikes findLike = postLikesRepository.findByUserAndPost(user, post).orElse(null);

        //좋아요가 되어있는지 아닌지 체크해서 등록/해제
        if(findLike == null){
            PostLikesRequestDto postLikesRequestDto = new PostLikesRequestDto(user, post);
            PostLikes postLikes = new PostLikes(postLikesRequestDto);
            postLikesRepository.save(postLikes);
        } else{
            postLikesRepository.deleteById(findLike.getId());
        }

        // 내가 참여한 게시글에 좋아요를 받았을 때
        alarmService.generatePostLikesAlarm(post);

        return new PostLikesResponseDto(postId, postLikesRepository.countByPost(post));
    }
}
