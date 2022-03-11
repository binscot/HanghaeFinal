package com.example.hanghaefinal.pubsub;

import com.example.hanghaefinal.dto.responseDto.ParagraphAccessResDto;
import com.example.hanghaefinal.model.Paragraph;
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
    //private final ChatMessageRepository chatMessageRepository;

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

            if (publishMessage.startsWith("ENTER", 9) || publishMessage.startsWith("QUIT", 9)) {
                log.info("------------------------ RedisSubscriber ENTER ----------- ");
                // 위에서 인자로 String으로 풀려져있는 publishMessage를 다시 Dto형태로 바꿔서 담는다.
                ParagraphAccessResDto paragraphAccessResDto = objectMapper.readValue(publishMessage, ParagraphAccessResDto.class);
                // 아래 desination이 프론트에서 구독한 주소이고 우리가 보내면 프론트에서 paragraphAccessResDto를 콜백으로 받는다 ㅇㅇ 그런듯
                messagingTemplate.convertAndSend("/sub/api/chat/rooms/" + paragraphAccessResDto.getPostId(), paragraphAccessResDto);
            }

            // 채팅방에서 채팅 시 메세지 보내기
            else if(publishMessage.startsWith("TALK", 9)){
                log.info("------------------------ RedisSubscriber TALK ----------- ");
                ParagraphAccessResDto paragraphAccessResDto = objectMapper.readValue(publishMessage, ParagraphAccessResDto.class);
                messagingTemplate.convertAndSend("/sub/api/chat/rooms/" + paragraphAccessResDto.getPostId(), paragraphAccessResDto);
                //ParagraphAccessResDto paragraphAccessResDto = objectMapper.readValue(publishMessage, ParagraphAccessResDto.class);
                //messagingTemplate.convertAndSend("/sub/api/chat/rooms/" + paragraphAccessResDto.getPostId(), paragraphAccessResDto);
            }

            // 채팅방에서 아이템 사용시 메세지 보내기
            else if(publishMessage.startsWith("START", 9)){
                log.info("------------------------ RedisSubscriber START ----------- ");
                ParagraphAccessResDto paragraphAccessResDto = objectMapper.readValue(publishMessage, ParagraphAccessResDto.class);
                messagingTemplate.convertAndSend("/sub/api/chat/rooms/" + paragraphAccessResDto.getPostId(), paragraphAccessResDto);
            }

            // Paragraph 객채로 맵핑
            //Paragraph paragraph = objectMapper.readValue(publishMessage, Paragraph.class);
            // 채팅방을 구독한 클라이언트에게 메시지 발송
            // 여기 destination 이 프론트에서 sub한 곳인데 우리는 우리가 이제 보내면 프론트에서 callback으로 받는 거지??? ㅇㅇ 그런듯
            //messagingTemplate.convertAndSend("/sub/api/chat/rooms/" + paragraph.getPost().getId(), paragraph);
            // 여기 roomId를 userId로 하는 것 고려
            // user마다 REDIS에...
            // redis에 저장하지 말고 repository에 저장하는 것 고려...
            // 최종적으로는 repository에...
            // 스케줄러로 db값을 바꿔주기만하고 새로고침 페이지 이동할 때만..
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
    }
}
