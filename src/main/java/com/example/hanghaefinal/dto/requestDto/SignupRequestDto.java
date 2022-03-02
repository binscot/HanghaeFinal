package com.example.hanghaefinal.dto.requestDto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SignupRequestDto {

    //회원가입을 위한 정보 username,paddword,email의 길이와객체들
    @NotBlank(message = "아이디를 입력해 주세요!")
    @Size(min = 3,max = 10, message = "아이디는 3자 이상 10자 이하로 입력해 주세요!")
    @Pattern(regexp= "^[a-zA-Z0-9]{3,20}$",message = "알파벳 대소문자, 숫자만 입력 가능합니다!")
    private String username;

    @NotBlank(message = "비밀번호를 입력해 주세요!")
    @Size(min = 4, max = 10, message = "비밀번호확인은 비밀번호와 똑같이 입력해주세요!")
    private String nickName;


    private MultipartFile userProfile;

    @NotBlank(message = "비밀번호를 입력해 주세요!")
    @Size(min = 4, max = 10, message = "비밀번호는 4자 이상 입력해주세요!")
    private String password;

    @NotBlank(message = "비밀번호를 입력해 주세요!")
    @Size(min = 4, max = 10, message = "비밀번호확인은 비밀번호와 똑같이 입력해주세요!")
    private String checkPassword;

    @Size(max = 150, message = "소개는 300자 이하로 작성해주세요!")
    private String introduction;

}
