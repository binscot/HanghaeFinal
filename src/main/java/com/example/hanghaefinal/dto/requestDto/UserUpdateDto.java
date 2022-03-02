package com.example.hanghaefinal.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

    private String nickName;

    private String introduction;

    private MultipartFile userProfile;

    private String password;

    private String passwordCheck;

}
