package com.example.hanghaefinal.dto.requestDto;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
public class PasswordRequestDto {

    @NotNull
    private String username;

    @NotNull
    @Size(min = 3, max = 20)
    private String password;

    @NotNull
    @Size(min = 3, max = 20)
    private String checkPassword;
}
