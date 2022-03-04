package com.example.hanghaefinal.dto.requestDto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
    @Getter
    @Setter
    @Data
    public class CommentRequestDto {

        private String comment;
    }
