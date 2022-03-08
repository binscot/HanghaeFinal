package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.Paragraph;
import lombok.Getter;

@Getter
public class ParagraphResDto {
    private String paragraph;

    public ParagraphResDto(Paragraph paragraph){
        this.paragraph = paragraph.getParagraph();
    }
}
