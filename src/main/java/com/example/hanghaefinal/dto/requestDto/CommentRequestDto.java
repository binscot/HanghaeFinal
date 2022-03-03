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

        @Size(min = 1,max = 200, message = "댓글은 200자 이내로 작성해주세요.")
        @NotBlank(message = "댓글을 입력해주세요.")
        private String comment;
    }
