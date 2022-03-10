package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.dto.requestDto.ParagraphReqDto;
import com.example.hanghaefinal.model.Paragraph;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParagraphAccessResDto {
    private Paragraph.MessageType type;
    private String postId;
    private String paragraph;
    private String nickName;

    public ParagraphAccessResDto(ParagraphReqDto paragraphReqDto) {
        this.type = paragraphReqDto.getType();
        this.postId = paragraphReqDto.getPostId();
        this.paragraph = paragraphReqDto.getParagraph();
        this.nickName = paragraphReqDto.getNickName();
    }


}
