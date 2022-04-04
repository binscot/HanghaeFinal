package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.Paragraph;
import lombok.*;

@Getter
@Setter
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuitResDto {
    private Paragraph.MessageType type;
    private String nickName; // message
}

