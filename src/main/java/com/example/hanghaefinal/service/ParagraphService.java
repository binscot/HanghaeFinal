package com.example.hanghaefinal.service;

import com.example.hanghaefinal.dto.requestDto.ParagraphLikesReqDto;
import com.example.hanghaefinal.dto.requestDto.ParagraphReqDto;
import com.example.hanghaefinal.dto.responseDto.ParagraphAccessResDto;
import com.example.hanghaefinal.dto.responseDto.ParagraphLikesClickUserKeyResDto;
import com.example.hanghaefinal.dto.responseDto.ParagraphLikesResDto;
import com.example.hanghaefinal.dto.responseDto.UserInfoResponseDto;
import com.example.hanghaefinal.exception.exception.*;
import com.example.hanghaefinal.model.Paragraph;
import com.example.hanghaefinal.model.ParagraphLikes;
import com.example.hanghaefinal.model.Post;
import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParagraphService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ParagraphRepository paragraphRepository;
    private final ParagraphLikesRepository paragraphLikesRepository;
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;
    private final AlarmService alarmService;
    public final AlarmRepository alarmRepository;
    private final LevelService levelService;

//    @Scheduled(fixedRate = 5000)
//    public void rateJob(Post post,int paragraphCnt) {
//        int nowParagraphCnt = paragraphRepository.countByPost(post);
//        if (paragraphCnt==nowParagraphCnt){
//            post.updatePostWriting(false, null,null);
//        }
//        log.info("schedule..");
//    }

    @Transactional
    public Boolean saveParagraph(ParagraphReqDto paragraphReqDto, Long postId, User user){
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException("???????????? ???????????? ????????????.")
        );
        if(paragraphReqDto.getParagraph().length() > 2000){
            throw new ParagraphLimitException("????????? 2000??? ????????? ??????????????????.");
        }
        if(paragraphReqDto.getParagraph().equals("")){
            throw new ContentNullException("????????? ??????????????????");
        }

        int limit = post.getLimitCnt();
        int paragraphListSize = paragraphRepository.findAllByPostId(postId).size();


        if (limit >= paragraphListSize ){
            // ????????? roomId??? ??????????????? post??? ???????????? ????????? postId??? ????????????.
            //Paragraph paragraph = new Paragraph(paragraphReqDto.getParagraph(), user, post);
            Paragraph paragraph = new Paragraph(paragraphReqDto, user, post);
            paragraphRepository.save(paragraph);

            //????????? ??????
            int userPoint = user.getPoint()+2;
            user.updatePoint(userPoint);
            userRepository.save(user);


            // ????????? ????????? ?????? ?????? ??? ?????? -
            alarmService.generateNewParagraphAlarm(user, post);

        } else throw new ParagraphCountException("?????? ????????? ??????????????????.");

        return true;
    }

    // destination ???????????? postId ??????
    public String getPostId(String destination) {
        // lastindexOf() ??? ?????? ????????? ???????????? ??????????????? ?????? ???????????? ???????????? ????????????.
        int lastIndex = destination.lastIndexOf('/');
        log.info("destination = {}", destination);
        if (lastIndex != -1) {
            log.info("destination roomId = {}", destination.substring(lastIndex +1));
            return destination.substring(lastIndex + 1);
        } else {
            return null;
        }
    }

    // ????????? ????????? ??? ????????? ??????
    public void accessChatMessage(ParagraphReqDto paragraphReqDto) {

        log.info("????????? ?????? ????????? ?????? ??? roomID = {}", paragraphReqDto.getPostId());
        User user = userRepository.findById(paragraphReqDto.getUserId())
                .orElseThrow(IllegalAccessError::new);
        log.info("service ?????? ?????? ??? user = {}", user);
        if (Paragraph.MessageType.ENTER.equals(paragraphReqDto.getType())) {
            paragraphReqDto.setParagraph(user.getNickName() + "?????? ?????? ??????????????????."); // ????????? ??????
            ParagraphAccessResDto paragraphAccessResDto = new ParagraphAccessResDto(paragraphReqDto);
            // RedisConfig??? listenerAdapter??? ??????.
            redisTemplate.convertAndSend(channelTopic.getTopic(), paragraphAccessResDto);
            // paragraphAccessResDto ??? RedisSubscriber??? sendMessage??? ????????? String ????????? ???????????????.

        } else if (Paragraph.MessageType.QUIT.equals(paragraphReqDto.getType())) {

            paragraphReqDto.setParagraph(user.getNickName());
            ParagraphAccessResDto paragraphAccessResDto = new ParagraphAccessResDto(paragraphReqDto);
            redisTemplate.convertAndSend(channelTopic.getTopic(), paragraphAccessResDto);

        }
    }

    // ???????????? ?????? ??????????????? response????????? ?????????
    public Boolean paragraphStartAndComplete(ParagraphReqDto paragraphReqDto, Long postId) {
        User user = userRepository.findById(paragraphReqDto.getUserId()).orElseThrow(
                ()-> new UserNotFoundException("???????????? ?????? ID ?????????.")
        );

        log.info("--------------------------- sendChatMessage user.getUsername() : " + user.getUsername());
        log.info("sendChatMessage user= {}", user);

        UserInfoResponseDto userInfoResDto = new UserInfoResponseDto(user);
        //ParagraphResDto paragraphResDto = new ParagraphResDto(paragraph, postId, user);
        ParagraphAccessResDto paragraphAccessResDto = new ParagraphAccessResDto(paragraphReqDto, userInfoResDto);

        log.info("-------------- paragraphAccessResDto :  " + paragraphAccessResDto);
        log.info("-------------------- userInfoResDto : " + userInfoResDto);

        // convertAndSend ??? ??? redis ??????????????? ???????????? (disconnect??? ?????? ?????????)
        redisTemplate.convertAndSend(channelTopic.getTopic(), paragraphAccessResDto);

        return true;
    }

    @Transactional
    public ParagraphLikesResDto paragraphLikes(Long paragraphId, Long userId){
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("???????????? ?????? ID ?????????.")
        );

        Paragraph paragraph = paragraphRepository.findById(paragraphId).orElseThrow(
                () -> new ParagraphNotFoundException("????????? ???????????? ????????????.")
        );

        User likedUser = paragraph.getUser();
        int userPoint = user.getPoint()+1;


        ParagraphLikes findParagraphLikes = paragraphLikesRepository.findByUserAndParagraph(user, paragraph).orElse(null);

        if(findParagraphLikes == null){
            ParagraphLikesReqDto paragraphLikesReqDto = new ParagraphLikesReqDto(user, paragraph);
            ParagraphLikes paragraphLikes = new ParagraphLikes(paragraphLikesReqDto);

            int likePoint = likedUser.getPoint()+1;
            likedUser.updatePoint(likePoint);

            paragraphLikesRepository.save(paragraphLikes);

            // ????????? ???????????? ????????? ?????? ??????????????? ????????? ????????? ??????.
            alarmService.generateParagraphLikestAlarm(paragraph.getUser(), paragraph.getPost());

        } else {
            paragraphLikesRepository.deleteById(findParagraphLikes.getId());

            int likePoint = likedUser.getPoint()-1;
            likedUser.updatePoint(likePoint);
        }

        List<ParagraphLikes> paragraphLikes = paragraphLikesRepository.findAllByParagraphId(paragraphId);
        List<ParagraphLikesClickUserKeyResDto> paragraphLikesClickUserKeyResDtoList = new ArrayList<>();
        for(ParagraphLikes paragraphLikeTemp : paragraphLikes){
            paragraphLikesClickUserKeyResDtoList.add(new ParagraphLikesClickUserKeyResDto(paragraphLikeTemp));
        }

        return new ParagraphLikesResDto(paragraphId, paragraphLikesClickUserKeyResDtoList, paragraphLikesRepository.countByParagraph(paragraph));
    }

}