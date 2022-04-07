package com.example.hanghaefinal.config.redis;

import com.example.hanghaefinal.dto.responseDto.AlarmResponseDto;
import com.example.hanghaefinal.dto.responseDto.ParagraphAccessResDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;
    // 타입을 stompHandler에서 잡는다?

    // Redis 에서 메시지가 발행(publish)되면 대기하고 있던 Redis Subscriber 가 해당 메시지를 받아 처리한다.
    // redisTemplate.convertAndSend(channelTopic.getTopic(), paragraphAccessResDto);
    // 즉, redisTemplate.convertAndSend 안에 있는 인자 paragraphAccessResDto를 sendMessage의 인자로 String으로 풀어버린다.
    public void sendMessage(String publishMessage) {
        log.info("RedisSubscriber sendMessage publishMessage = {}", publishMessage);
        log.info("---------------- publishMessage 새롭게 자른거 = {} :", publishMessage.startsWith("TALK"));
        log.info("publishMessage 새롭게 자른거 = {}", publishMessage.startsWith("ENTER", 9));
        log.info("publishMessage 새롭게 자른거 = {}", publishMessage.startsWith("TALK", 9));
        log.info("publishMessage 새롭게 자른거 = {}", publishMessage.startsWith("START", 9));
        try {
            // if 타입이 alarm 이면 똑같이 원하는 주소로 dto를 보내주면 된다. ㅇㅇ

            if (publishMessage.startsWith("ENTER", 9)) {
                log.info("------------------------ RedisSubscriber ENTER ----------- ");
                // 위에서 인자로 String으로 풀려져있는 publishMessage를 다시 Dto형태로 바꿔서 담는다.
                ParagraphAccessResDto paragraphAccessResDto = objectMapper.readValue(publishMessage, ParagraphAccessResDto.class);
                // 아래 desination이 프론트에서 구독한 주소이고 우리가 보내면 프론트에서 paragraphAccessResDto를 콜백으로 받는다 ㅇㅇ 그런듯
                messagingTemplate.convertAndSend("/sub/api/chat/rooms/" + paragraphAccessResDto.getPostId(), paragraphAccessResDto);
            }

            // '문단 완료' 버튼 누를 시 responseDto 보내기 보내기
            else if(publishMessage.startsWith("TALK", 9)){
                log.info("------------------------ RedisSubscriber TALK ----------- ");
                ParagraphAccessResDto paragraphAccessResDto = objectMapper.readValue(publishMessage, ParagraphAccessResDto.class);
                messagingTemplate.convertAndSend("/sub/api/chat/rooms/" + paragraphAccessResDto.getPostId(), paragraphAccessResDto);
            }

            // '문단 시작' 버튼 누를 시 responseDto 보내기
            else if(publishMessage.startsWith("START", 9)){
                log.info("------------------------ RedisSubscriber START ----------- ");

                ParagraphAccessResDto paragraphAccessResDto = objectMapper.readValue(publishMessage, ParagraphAccessResDto.class);
                messagingTemplate.convertAndSend("/sub/api/chat/rooms/" + paragraphAccessResDto.getPostId(), paragraphAccessResDto);
            } else if (publishMessage.contains("[알림]")){
                log.info("------------------------ publishMessage : " + publishMessage);
                log.info("----------------------- 알림이 들어오긴 하나");
                AlarmResponseDto alarmResponseDto = objectMapper
                        .readValue(publishMessage, AlarmResponseDto.class);

                messagingTemplate.convertAndSend(
                        "/sub/alarm/" + alarmResponseDto.getAlarmTargetId(),
                        alarmResponseDto);
            } else if(publishMessage.startsWith("QUIT", 9)){
                ParagraphAccessResDto paragraphAccessResDto = objectMapper.readValue(publishMessage, ParagraphAccessResDto.class);
                messagingTemplate.convertAndSend("/sub/api/chat/rooms/" + paragraphAccessResDto.getPostId(), paragraphAccessResDto);
            }

        } catch (Exception e) {
            log.error("Exception {}", e);
        }
    }
}