package com.example.hanghaefinal.service;

import com.example.hanghaefinal.dto.requestDto.PostLikesRequestDto;
import com.example.hanghaefinal.dto.responseDto.PostLikeClickersResponseDto;
import com.example.hanghaefinal.dto.responseDto.PostLikesResponseDto;
import com.example.hanghaefinal.exception.exception.PostNotFoundException;
import com.example.hanghaefinal.exception.exception.UserNotFoundException;
import com.example.hanghaefinal.model.Alarm;
import com.example.hanghaefinal.model.Post;
import com.example.hanghaefinal.model.PostLikes;
import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.repository.AlarmRepository;
import com.example.hanghaefinal.repository.PostLikesRepository;
import com.example.hanghaefinal.repository.PostRepository;
import com.example.hanghaefinal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostLikesService {

    private final PostLikesRepository postLikesRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final AlarmService alarmService;
    private final AlarmRepository alarmRepository;
    private final LevelService levelService;

    //좋아요 등록
    @Transactional
    public PostLikesResponseDto addLike(Long postId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("존재하지 않는 ID 입니다.")
        );

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException("게시물이 존재하지 않습니다.")
        );

        User likedUser = userRepository.findById(postId).orElseThrow(
                () -> new UserNotFoundException("존재하지 않는 ID입니다.")
        );

        PostLikes findLike = postLikesRepository.findByUserAndPost(user, post).orElse(null);

        //좋아요가 되어있는지 아닌지 체크해서 등록/해제
        if (findLike == null) {
            PostLikesRequestDto postLikesRequestDto = new PostLikesRequestDto(user, post);
            PostLikes postLikes = new PostLikes(postLikesRequestDto);
            likedUser.setPoint(likedUser.getPoint()+1);
            levelService.LevelCheck(user);
            postLikesRepository.save(postLikes);

            // 내가 참여한 게시글에 좋아요를 받았을 때
            log.info("---------------------- 444444aaaa ----------------------");
            alarmService.generatePostLikesAlarm(post);

        } else {
            postLikesRepository.deleteById(findLike.getId());
            likedUser.setPoint(likedUser.getPoint()-1);
            levelService.LevelCheck(user);
        }

        List<PostLikes> postLikes = postLikesRepository.findAllByPostId(postId);
        List<PostLikeClickersResponseDto> postLikeClickersResponseDtos = new ArrayList<>();
        for (PostLikes postLikesTemp : postLikes) {
            postLikeClickersResponseDtos.add(new PostLikeClickersResponseDto(postLikesTemp));
        }








        return  new PostLikesResponseDto(postId, postLikeClickersResponseDtos,postLikesRepository.countByPost(post));
    }
}
