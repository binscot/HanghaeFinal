package com.example.hanghaefinal.config.socket;


import com.example.hanghaefinal.dto.requestDto.ParagraphReqDto;
import com.example.hanghaefinal.exception.exception.PostNotFoundException;
import com.example.hanghaefinal.model.Paragraph;
import com.example.hanghaefinal.model.Post;
import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.repository.ParagraphRepository;
import com.example.hanghaefinal.repository.PostRepository;
import com.example.hanghaefinal.repository.RedisRepository;
import com.example.hanghaefinal.repository.UserRepository;
import com.example.hanghaefinal.security.jwt.JwtTokenProvider;
import com.example.hanghaefinal.service.ParagraphService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Component
@Transactional
@Slf4j
public class StompHandler implements ChannelInterceptor {
    private final ParagraphService paragraphService;
    private final RedisRepository redisRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PostRepository postRepository;

    // websocket 을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // substring 해줘야 토큰만 제대로 빼내올 수 있을 거다 - 이건 확인하자
        // 토큰의 값만 확인 (로그인 여부를 확인하기 위함) - 헤더의 토큰값을 빼오기
        String token = accessor.getFirstNativeHeader("Authorization");

        // 로그인 안한 사용자 예외처리 필요
        String username = jwtTokenProvider.getAuthentication(token).getName();
        Optional<User> user = userRepository.findByUsername(username);

        // websocket 연결시 헤더의 jwt token 검증 ( 만약 CONNECT라면 -> 초기 연결임 )

        if (StompCommand.CONNECT == accessor.getCommand()) {
            log.info("~~~~~~~~ CONNECT 할 때 username = {}", username);
        }

        // 구독 했는지 확인
        else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            log.info("~~~~~~~~ SUBSCRIBE 할 때 token = {}", token );
            String postId = paragraphService.getPostId(
                    Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId")
            );
            // 채팅방에 들어온 클라이언트 sessionId를 postId와 맵핑해 놓는다.(나중에 특정 세션이 어떤 채팅방에 들어가 있는지 알기 위함)
            if (postId != null) {
//                String username = jwtDecoder.decodeUsername(token);
                Long userId = user.get().getId();
                String sessionId = (String) message.getHeaders().get("simpSessionId");
                // 채팅방에 들어온 클라이언트 sessionId를 roomId와 맵핑해 놓는다.(나중에 특정 세션이 어떤 채팅방에 들어가 있는지 알기 위함)
                if(username != null) {
                    // sessionId로 inOutKey 등록
                    // setUserEnterInfo() 메서드에서 유저가 입장한 채팅방ID 와 유저세션ID, username 맵핑 정보 저장 ( redis에 저장 )
                    redisRepository.setSessionUserInfo(sessionId, postId, username);
                    // inOutKey로 현재 유저가 접속 중인지 설정
                    redisRepository.setUserChatRoomInOut(postId + "_" + username, true);
                    log.info("~~~~~~~~ SUBSCRIBE 할 때 postId = {}", postId);
                    log.info("~~~~~~~~ SUBSCRIBE 할 때 sessionId = {}", sessionId);
                    log.info("~~~~~~~~ SUBSCRIBE 할 때 username = {}", username);

                    // 구독했다는 것은 처음 입장했다는 것이므로 입장 메시지를 발송한다.
                    // 클라이언트 입장 메시지를 채팅방에 발송한다.(redis publish)
                    // 이 코드 필요없음 ( 프론트에서 안쓰면 그만 )
                    paragraphService.accessChatMessage(ParagraphReqDto.builder().type(Paragraph.MessageType.ENTER).postId(postId).userId(userId).build());
                    log.info("~~~~~~~~ TYPE Enter 일 때");
                } else throw new IllegalArgumentException("로그인을 해주시기 바랍니다.");
            }

        }

        // disconnect 확인
        else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~ DISCONNECT");
            String postId = paragraphService.getPostId(
                    Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId")
            );
            Long userId = user.get().getId();
            Post post = postRepository.findById(Long.valueOf(postId)).orElseThrow(
                    () -> new PostNotFoundException("게시물이 존재하지 않습니다.")
            );
            if (post.getWriter().equals(user.get().getNickName()) && post.isWriting()){
                log.info("isWriting---------------------------------------false"+"post.getWriter()=="+post.getWriter()+"user.get().getNickName()=="+user.get().getNickName());
                post.updatePostWriting(false, null,null);
            }

            String sessionId = (String) message.getHeaders().get("simpSessionId");
            String findInOutKey = redisRepository.getSessionUserInfo(sessionId);

            if (findInOutKey != null) {
                redisRepository.setUserChatRoomInOut(findInOutKey, false);
            }
            redisRepository.removeUserEnterInfo(sessionId);
            //그 사람이 글을 start 를 눌렀던 유저라면 post의 writing 상태값을 변경 해줘야한다

            // disconnect 됐다는 메시지는 주지 말자
            paragraphService.accessChatMessage(ParagraphReqDto.builder().type(Paragraph.MessageType.QUIT).postId(postId).userId(userId).build());
        }
        return message;
    }
}