package com.example.hanghaefinal.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

    private String nickName;

    private String introduction;

    private String password;

    private String passwordCheck;

}
