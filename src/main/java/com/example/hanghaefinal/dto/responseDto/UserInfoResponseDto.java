package com.example.hanghaefinal.dto.responseDto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserInfoResponseDto {
    private Long id;
    private String username;
    private Boolean is_login;
}
