package com.example.hanghaefinal.dto.requestDto;

import lombok.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;

@Setter
@Getter
@Validated
public class SignupRequestDto {

    //회원가입을 위한 정보 username,paddword,email의 길이와객체들
    @NotBlank(message = "아이디를 입력해 주세요!")
    @Email(message = "이메일 형식으로 입력해 주세요!")
    private String username;

    @NotBlank(message = "닉네임을 입력해 주세요!")
    @Size(min = 4, max = 15, message = "닉네임은 4자 이상 15자 이하로 입력해주세요!")
    private String nickName;

    private MultipartFile userProfile;

    @NotBlank(message = "비밀번호를 입력해 주세요!")
    @Size(min = 4, max = 20, message = "비밀번호는 4자 이상 입력해주세요!")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해 주세요!")
    @Size(min = 4, max = 20, message = "비밀번호확인은 비밀번호와 똑같이 입력해주세요!")
    private String checkPassword;

    @Size(max = 150, message = "소개는 300자 이하로 작성해주세요!")
    private String introduction;

}
