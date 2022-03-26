package com.example.hanghaefinal.controller;

import com.example.hanghaefinal.dto.requestDto.ParagraphReqDto;
import com.example.hanghaefinal.dto.responseDto.ParagraphLikesResDto;
import com.example.hanghaefinal.model.Paragraph;
import com.example.hanghaefinal.model.Post;
import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.repository.UserRepository;
import com.example.hanghaefinal.security.UserDetailsImpl;
import com.example.hanghaefinal.security.jwt.JwtTokenProvider;
import com.example.hanghaefinal.service.ParagraphService;
import com.example.hanghaefinal.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ParagraphController {

    private final ParagraphService paragraphService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PostService postService;

    // 문단 생성
    /*@PostMapping("/paragraph/{postId}")
    public Boolean saveParagraph(@PathVariable Long postId,
                                 @RequestBody ParagraphReqDto paragraphReqDto,
                                 @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        if(userDetails != null) {
            User user = userDetails.getUser();
            paragraphService.saveParagraph(paragraphReqDto, postId, user);
        } else throw new IllegalArgumentException("로그인한 유저 정보가 없습니다.");
        return true;
    }*/

    // 작성 취소 로직
    @PostMapping("/cancelIsWriting/{postId}")
    public Boolean cancelIsWriting(@PathVariable Long postId){
        return postService.cancelIsWriting(postId);
    }

    // 예를들어 좋아요 알림 같은 것도 controller에 @PostMapping으로 만들 수 있다.
    // 위에 api 를 대체해야한다.
    // 채팅 메시지를 @MessageMapping 형태로 받는다
    // 웹소켓으로 publish 된 메시지를 받는 곳이다 ( 프론트에서 '/pub/api/chat/message', 이런식으로 pub 준다.)
    // 이게 pub로 받는 api이다 이거 알림 같은 경우는 @PostMapping 해야할듯
    // '문단 생성 완료' 버튼 누를 때
    @MessageMapping("/paragraph/complete")   // 참고하느 코드는 roomId ReqDto에 넣었다. 즉, 연관관계를 안맺음
    public void message(
            @RequestBody ParagraphReqDto paragraphReqDto,
            @Header("Authorization") String rawToken
            //@AuthenticationPrincipal UserDetailsImpl userDetails
            //userDetails 이거 못쓰면 토큰에서 가져와야 할듯
    ) {
        //String token = rawToken.substring(7); // Bearer 때문에 한듯
        if (paragraphReqDto.getParagraph()==null){
            throw new NullPointerException("문단을 작성해 주세요!");
        }
        Long postId = Long.valueOf(paragraphReqDto.getPostId());
        String token = rawToken;
        String username = jwtTokenProvider.getAuthentication(token).getName();
        //Optional<User> user = userRepository.findByUsername(username);
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("유저네임이 존재하지 않습니다.")
        );

        log.info("~~~~~~~~~~~~~~~~~~~~~~/chat/message/ 안에서 token : " + token+"\n");
        // 로그인 회원 정보를 들어온 메시지에 값 세팅
        //paragraphReqDto.setUserId(user.get().getId());
        paragraphReqDto.setUserId(user.getId());
        //Long userId = user.get().getId();

        // MySql DB에 채팅 메시지 저장
        // redis에 만 저장하면 다른 사람이 새로고침하면 날라가니까.. 저장을 해야한다.
        // TALK 할 때만 save 근데.. 여기서 save 해준 이유 확인하자...

        //ChatMessage chatMessage = chatMessageService.save(chatMessageRequestDto);

        // 웹소켓 통신으로 게시글 안에 있는 사람들한테 response데이터 보내기
        if(paragraphReqDto.getType().equals(Paragraph.MessageType.START)){
            log.info("---------------- START START START ---------");
            paragraphService.paragraphStartAndComplete(paragraphReqDto, postId);
            postService.startWritingStatus(postId, user);
        }
        else if(paragraphReqDto.getType().equals(Paragraph.MessageType.TALK)) {
            log.info("---------------TALK TALK TALK ----------------");
            paragraphService.saveParagraph(paragraphReqDto, postId, user);
            paragraphService.paragraphStartAndComplete(paragraphReqDto, postId);
            postService.talkWritingStatus(postId);
        }
//        else if(paragraphReqDto.getType().equals(Paragraph.MessageType.ENTER))
//            paragraphService.sendChatMessage();

    }

    @PostMapping("/paragraph/likes/{paragraphId}")
    public ParagraphLikesResDto paragraphLikes(@PathVariable Long paragraphId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return paragraphService.paragraphLikes(paragraphId, userDetails.getUser().getId());
    }
}