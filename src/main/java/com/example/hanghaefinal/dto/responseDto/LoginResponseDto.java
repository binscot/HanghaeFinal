package com.example.hanghaefinal.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    //로그인 되었을때 리턴되는객체들 토큰발행
    //private String token;
    private Long userKey;
    private String username;
    private String nickname;
    private String userProfileImage;
    private String introduction;
}