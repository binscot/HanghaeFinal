package com.example.hanghaefinal.service;

import com.example.hanghaefinal.dto.requestDto.ParagraphLikesReqDto;
import com.example.hanghaefinal.dto.requestDto.ParagraphReqDto;
import com.example.hanghaefinal.dto.responseDto.*;
import com.example.hanghaefinal.exception.exception.*;
import com.example.hanghaefinal.model.*;
import com.example.hanghaefinal.repository.*;
import com.example.hanghaefinal.security.UserDetailsImpl;
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

    @Transactional
    public Boolean saveParagraph(ParagraphReqDto paragraphReqDto, Long postId, User user){
        log.info("---------------------- 랙규야~~~~~~~~~~밥먹자1111111111 ----------------------");
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException("게시물이 존재하지 않습니다.")
        );
        log.info("---------------------- 랙규야~~~~~~~~~~밥먹자2222222222 ----------------------");
        if(paragraphReqDto.getParagraph().length() > 2000){
            throw new ParagraphLimitException("문단은 2000자 이내로 입력해주세요.");
        }
        log.info("---------------------- 랙규야~~~~~~~~~~밥먹자3333333333333----------------------");
        if(paragraphReqDto.getParagraph().equals("")){
            throw new ContentNullException("문단을 작성해주세요");
        }

        int limit = post.getLimitCnt();
        log.info("---------------------- 랙규야~~~~~~~~~~밥먹자44444444444444 ----------------------");
        int paragraphListSize = paragraphRepository.findAllByPostId(postId).size();
        log.info("---------------------- 랙규야~~~~~~~~~~밥먹자555555555555 ----------------------");


        log.info("------------------------- paragraphListSize --------------------- : " + paragraphListSize);
        log.info("------------------------- paragraphListSize --------------------- : " + paragraphListSize);
        log.info("------------------------- paragraphListSize --------------------- : " + paragraphListSize);
//        int countBySize = paragraphRepository.countByParagraph(postId);
//        log.info("------------------------- countBySize ------------------------- : " + countBySize);
//        log.info("------------------------- countBySize ------------------------- : " + countBySize);
//        log.info("------------------------- countBySize ------------------------- : " + countBySize);


        if (limit >= paragraphListSize ){
            log.info("---------------------- 랙규야~~~~~~~~~~밥먹자666666666666 ----------------------");
            // 우리는 roomId를 저장안하고 post와 연관관계 맺어서 postId를 저장한다.
            //Paragraph paragraph = new Paragraph(paragraphReqDto.getParagraph(), user, post);
            Paragraph paragraph = new Paragraph(paragraphReqDto, user, post);
            log.info("---------------------- 랙규야~~~~~~~~~~밥먹자7777777777777777 ----------------------");
            paragraphRepository.save(paragraph);

            //포인트 추가
            int userPoint = user.getPoint()+2;
            user.updatePoint(userPoint);
            userRepository.save(user);


            log.info("---------------------- 111111aaaa ----------------------");
            // 소설에 문단이 등록 됐을 때 알림 -
            alarmService.generateNewParagraphAlarm(user, post);

        } else throw new ParagraphCountException("문단 개수를 초과했습니다.");

        return true;
    }

    // destination 정보에서 postId 추출
    public String getPostId(String destination) {
        // lastindexOf() 는 특정 문자나 문자열이 뒤에서부터 처음 발견되는 인덱스를 반환한다.
        int lastIndex = destination.lastIndexOf('/');
        log.info("destination = {}", destination);
        if (lastIndex != -1) {
            log.info("destination roomId = {}", destination.substring(lastIndex +1));
            return destination.substring(lastIndex + 1);
        } else {
            return null;
        }
    }

    // 채팅방 입출입 시 메시지 발송
    public void accessChatMessage(ParagraphReqDto paragraphReqDto) {

        log.info("채팅방 출입 메세지 발송 시 roomID = {}", paragraphReqDto.getPostId());
        User user = userRepository.findById(paragraphReqDto.getUserId())
                .orElseThrow(IllegalAccessError::new);
        log.info("service 넘어 왔을 때 user = {}", user);
        if (Paragraph.MessageType.ENTER.equals(paragraphReqDto.getType())) {
            paragraphReqDto.setParagraph(user.getNickName() + "님이 방에 입장했습니다."); // 안해도 될듯
            ParagraphAccessResDto paragraphAccessResDto = new ParagraphAccessResDto(paragraphReqDto);
            // RedisConfig의 listenerAdapter로 간다.
            redisTemplate.convertAndSend(channelTopic.getTopic(), paragraphAccessResDto);
            // paragraphAccessResDto 를 RedisSubscriber의 sendMessage의 인자로 String 형태로 풀어버린다.

        } else if (Paragraph.MessageType.STOP.equals(paragraphReqDto.getType())) {
//            paragraphReqDto.setParagraph(user.getNickName());
//            ParagraphAccessResDto paragraphAccessResDto = new ParagraphAccessResDto(paragraphReqDto);
            QuitResDto quitResDto = new QuitResDto();
            quitResDto.setType(Paragraph.MessageType.STOP);
            quitResDto.setNickName(user.getNickName());
            quitResDto.setPostId(paragraphReqDto.getPostId());
            redisTemplate.convertAndSend(channelTopic.getTopic(), quitResDto);
        }
    }

    // 게시글에 있는 사람들에게 response데이터 보내기
    public Boolean paragraphStartAndComplete(ParagraphReqDto paragraphReqDto, Long postId) {
        User user = userRepository.findById(paragraphReqDto.getUserId()).orElseThrow(
                ()-> new UserNotFoundException("존재하지 않는 ID 입니다.")
        );

        log.info("--------------------------- sendChatMessage user.getUsername() : " + user.getUsername());
        log.info("sendChatMessage user= {}", user);

        UserInfoResponseDto userInfoResDto = new UserInfoResponseDto(user);
        //ParagraphResDto paragraphResDto = new ParagraphResDto(paragraph, postId, user);
        ParagraphAccessResDto paragraphAccessResDto = new ParagraphAccessResDto(paragraphReqDto, userInfoResDto);

        log.info("-------------- paragraphAccessResDto :  " + paragraphAccessResDto);
        log.info("-------------------- userInfoResDto : " + userInfoResDto);

        // convertAndSend 할 때 redis 인메모리에 들어간다 (disconnect가 되면 없어짐)
        redisTemplate.convertAndSend(channelTopic.getTopic(), paragraphAccessResDto);

        return true;
    }

    @Transactional
    public ParagraphLikesResDto paragraphLikes(Long paragraphId, Long userId){
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("존재하지 않는 ID 입니다.")
        );

        Paragraph paragraph = paragraphRepository.findById(paragraphId).orElseThrow(
                () -> new ParagraphNotFoundException("문단이 존재하지 않습니다.")
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

            log.info("---------------------- 333333aaaa ----------------------");
            // 문단이 좋아요를 받으면 문단 작성자에게 좋아요 알림이 간다.
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
