package com.example.hanghaefinal.kakao;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class KakaoUserInfo {
    Long id;
    String email;
    String nickname;

    public KakaoUserInfo(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }
}
