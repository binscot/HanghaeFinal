package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OtherUserResDto2 {
    private Long userKey;
    private String username;
    private String nickname;
    private String userProfileImage;
    private String introduction;
    private List<PostResponseDto> postResponseDtoList;

    public OtherUserResDto2(User user, List<PostResponseDto> postResponseDtoList){
        this.userKey = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickName();
        this.userProfileImage = user.getUserProfileImage();
        this.introduction = user.getIntroduction();
        this.postResponseDtoList = postResponseDtoList;
    }
}
