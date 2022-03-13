package com.example.hanghaefinal.dto.requestDto;

import com.example.hanghaefinal.model.Paragraph;
import com.example.hanghaefinal.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ParagraphLikesReqDto {
    private User user;
    private Paragraph paragraph;
}
