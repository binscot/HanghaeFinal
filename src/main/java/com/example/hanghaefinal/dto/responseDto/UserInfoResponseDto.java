package com.example.hanghaefinal.dto.responseDto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserInfoResponseDto {
    private Long userKey;
    private String username;
    private String nickname;
    private String userProfileImage;
    private String  introduction;
}
