package com.example.hanghaefinal.service;

import com.example.hanghaefinal.Enum.AlarmType;
import com.example.hanghaefinal.dto.responseDto.AlarmResponseDto;
import com.example.hanghaefinal.model.Alarm;
import com.example.hanghaefinal.model.Paragraph;
import com.example.hanghaefinal.model.Post;
import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.repository.AlarmRepository;
import com.example.hanghaefinal.repository.ParagraphRepository;
import com.example.hanghaefinal.repository.PostRepository;
import com.example.hanghaefinal.repository.UserRepository;
import com.example.hanghaefinal.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;
    private final ParagraphRepository paragraphRepository;





    /* 베스트잽 선정 알림 보내기 */
//    public void generatePickedAlarm(Post post, Comment comment) {
//        Alarm alarm = Alarm.builder()
//                .userId(comment.getUser().getId())
//                .type(AlarmType.PICKED)
//                .postId(comment.getPost().getId())
//                .alarmMessage("["
//                        + post.getTitle()
//                        + "] 생드백에서 내 잽이 베스트 잽으로"
//                        + " 선정되었습니다.")
//                .isRead(false)
//                .build();
//
//        alarmRepository.save(alarm);
//
//        /* 알림 메시지를 보낼 DTO 생성 */
//        AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
//                .alarmId(alarm.getId())
//                .type(alarm.getType().toString())
//                .postId(alarm.getPostId())
//                .message("[알림] ["
//                        + post.getTitle()
//                        + "] 생드백에서 내 잽이 베스트 잽으로"
//                        + " 선정되었습니다.")
//                .alarmTargetId(alarm.getUserId())
//                .isRead(alarm.getIsRead())
//                .build();
//
//        redisTemplate.convertAndSend(channelTopic.getTopic(),
//                alarmResponseDto);
//    }

    /* 알림 목록 */
    public List<AlarmResponseDto> getAlamList(User user, int page, int size) {
        Long userId = user.getId();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt")
                .descending());

        List<Alarm> alarmListPage = alarmRepository
                .findAllByUserIdOrderByIdDesc(userId, pageable).getContent();

        List<AlarmResponseDto> alarmResponseDtoList = new ArrayList<>();

        for (Alarm alarm : alarmListPage) {
//            /* 채팅룸 생성알림일 때 */
//            if (alarm.getType().equals(AlarmType.INVITEDCHAT)) {
//                AlarmResponseDto alarmDto = AlarmResponseDto.builder()
//                        .alarmId(alarm.getId())
//                        .type(alarm.getType().toString())
//                        .message(alarm.getAlarmMessage())
//                        .isRead(alarm.getIsRead())
//                        .chatRoomId((String) postRepository
//                                .findByPubUserIdAndSubUserId(
//                                        alarm.getPubId(),
//                                        user.getId()).getId())
//                        .build();
//                alarmResponseDtoList.add(alarmDto);
                /* 게시물에 새로운 댓글이 등록되었을 때 */
            if (alarm.getType().equals(AlarmType.NEWPARAGRAPH)) {
                AlarmResponseDto alarmDto = AlarmResponseDto.builder()
                        .alarmId(alarm.getId())
                        .type(alarm.getType().toString())
                        .message(alarm.getAlarmMessage())
                        .isRead(alarm.getIsRead())
                        .postId(alarm.getPostId())
                        .build();
                alarmResponseDtoList.add(alarmDto);
                /* 내 잽이 작성자에게 선택받은 후 종료되었을 때 */
            }
//            else if (alarm.getType().equals(AlarmType.PICKED)) {
//                AlarmResponseDto alarmDto = AlarmResponseDto.builder()
//                        .alarmId(alarm.getId())
//                        .type(alarm.getType().toString())
//                        .message(alarm.getAlarmMessage())
//                        .isRead(alarm.getIsRead())
//                        .postId(alarm.getPostId())
//                        .build();
//                alarmResponseDtoList.add(alarmDto);
//                /* 레벨업 했을 때 */
//            }
        }
        return alarmResponseDtoList;
    }

    /* 게시물에 댓글이 등록되었을 경우 알림 보내기 */
//    public void generateNewReplyAlarm(User postOwner, User user, Post post) {
//        Alarm alarm = Alarm.builder()
//                .userId(postOwner.getId())
//                .type(AlarmType.REPLY)
//                .postId(post.getId())
//                .isRead(false)
//                .alarmMessage("[알림] ["
//                        + post.getTitle()
//                        + "] 게시글에 잽이 등록되었습니다. 확인해보세요.")
//                .build();
//
//        /* 알림 메시지를 보낼 DTO 생성 */
//        AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
//                .alarmId(alarm.getId())
//                .type(alarm.getType().toString())
//                .message("[알림] ["
//                        + post.getTitle()
//                        + "] 게시글에 잽이 등록되었습니다. 확인해보세요.")
//                .alarmTargetId(postOwner.getId())
//                .isRead(alarm.getIsRead())
//                .postId(alarm.getPostId())
//                .build();
//
//        /*-
//         * redis로 알림메시지 pub, alarmRepository에 저장
//         * 단, 게시글 작성자와 댓글 작성자가 일치할 경우는 제외
//         */
//        if (!alarmResponseDto.getAlarmTargetId().equals(user.getId())) {
//            alarmRepository.save(alarm);
//            redisTemplate.convertAndSend(channelTopic.getTopic(),
//                    alarmResponseDto);
//        }
//    }

//    내가 참여한 소설에 새로운 문단이 달렸을 경우
    public void generateNewReplyAlarm(User paragraphOwner, Post post) {

        List<Paragraph> paragraphList = paragraphRepository.findAllByPostId(post.getId());

        for (Paragraph paragraph:paragraphList){
            if (!Objects.equals(paragraph.getUser().getId(), paragraphOwner.getId())){
                Alarm alarm = Alarm.builder()
                        .userId(paragraph.getUser().getId())
                        .type(AlarmType.NEWPARAGRAPH)
                        .postId(post.getId())
                        .isRead(false)
                        .alarmMessage("[알림] ["
                                + post.getTitle()
                                + "] 소설에 문단이 등록되었습니다. 확인해보세요!")
                        .build();

                /* 알림 메시지를 보낼 DTO 생성 */
                AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
                        .alarmId(alarm.getId())
                        .type(alarm.getType().toString())
                        .message("[알림] ["
                                + post.getTitle()
                                + "] 소설에 문단이 등록되었습니다. 확인해보세요!")
                        .alarmTargetId(paragraph.getUser().getId())
                        .isRead(alarm.getIsRead())
                        .postId(alarm.getPostId())
                        .build();
                /*-
                 * redis로 알림메시지 pub, alarmRepository에 저장
                 * 단, 게시글 작성자와 댓글 작성자가 일치할 경우는 제외
                 */
                alarmRepository.save(alarm);
                redisTemplate.convertAndSend(channelTopic.getTopic(),
                        alarmResponseDto);
            }
        }



    }

    /* 알림 읽었을 경우 체크 */
    @Transactional
    public AlarmResponseDto alarmReadCheck(Long alarmId,
                                           UserDetailsImpl userDetails) {
        Alarm alarm = alarmRepository.getById(alarmId);
        User user = userDetails.getUser();
        alarm.setIsRead(true);
        alarmRepository.save(alarm);
        AlarmResponseDto alarmDto = new AlarmResponseDto();

        /* 새로운 채팅방에 초대 받았을 경우 */
//        if (alarm.getType().equals(AlarmType.INVITEDCHAT)) {
//            alarmDto = AlarmResponseDto.builder()
//                    .alarmId(alarm.getId())
//                    .type(alarm.getType().toString())
//                    .message(alarm.getAlarmMessage())
//                    .isRead(alarm.getIsRead())
//                    .chatRoomId((String) postRepository
//                            .findByPubUserIdAndSubUserId(
//                                    alarm.getPubId(),
//                                    user.getId()).getId())
//                    .build();
            /* 게시물에 새로운 댓글이 등록되었을 때 */
        if (alarm.getType().equals(AlarmType.NEWPARAGRAPH)) {
            alarmDto = AlarmResponseDto.builder()
                    .alarmId(alarm.getId())
                    .type(alarm.getType().toString())
                    .message(alarm.getAlarmMessage())
                    .isRead(alarm.getIsRead())
                    .postId(alarm.getPostId())
                    .build();
            /* 내 댓글이 작성자에게 선택받았을 때 */
        }
//        else if (alarm.getType().equals(AlarmType.PICKED)) {
//            alarmDto = AlarmResponseDto.builder()
//                    .alarmId(alarm.getId())
//                    .type(alarm.getType().toString())
//                    .message(alarm.getAlarmMessage())
//                    .isRead(alarm.getIsRead())
//                    .postId(alarm.getPostId())
//                    .build();
//            /* 레벨업 했을 때 */
//        } else {
//            alarmDto = AlarmResponseDto.builder()
//                    .alarmId(alarm.getId())
//                    .type(alarm.getType().toString())
//                    .message(alarm.getAlarmMessage())
//                    .isRead(alarm.getIsRead())
//                    .build();
//        }

        return alarmDto;
    }
}
