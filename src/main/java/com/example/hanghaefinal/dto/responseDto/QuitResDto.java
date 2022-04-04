package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.Paragraph;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuitResDto {
    private Paragraph.MessageType type;
    private String nickName; // message

}

