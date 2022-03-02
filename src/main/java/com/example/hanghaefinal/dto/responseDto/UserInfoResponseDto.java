package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserInfoResponseDto {
    private Long userKey;
    private String username;
    private String nickname;
    private String userProfileImage;
    private String  introduction;




    public UserInfoResponseDto(User user){
        this.userKey = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickName();
        this.userProfileImage = user.getUserProfileImage();
        this.introduction = user.getIntroduction();
    }

}
