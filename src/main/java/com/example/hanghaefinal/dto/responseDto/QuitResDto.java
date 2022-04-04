package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.Paragraph;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class QuitResDto {
    private Paragraph.MessageType type;
    private String nickName; // message
}

