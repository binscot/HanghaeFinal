package com.example.hanghaefinal.dto.requestDto;

import com.example.hanghaefinal.model.Paragraph;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParagraphReqDto {
    private Paragraph.MessageType type;
    //private String roomId;    // 이건 왜 String으로 했어? - 아하  sendMessage(String publishMessage) { 에서 전부 String으로 들어와서
    private String postId; // roomId를 대체 - postId를 일단 String으로 request안에 담아서 달라고 해야겠네 
    private String paragraph; // message
    private String nickName;
    private Long userId;        // 이건 왜 넣음? - 채팅을 작성한 사람의 유저pk
}
