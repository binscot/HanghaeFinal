package com.example.hanghaefinal.dto.responseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CommentResponseDto {
    private Long commentId;
    private String comment;
    private String commentModifiedAt;
    private Long commentUserId;
    private UserInfoResponseDto userInfoResponseDto;
}
