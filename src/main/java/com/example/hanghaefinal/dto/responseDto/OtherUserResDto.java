package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.Post;
import com.example.hanghaefinal.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OtherUserResDto {
    private Long userKey;
    private String username;
    private String nickname;
    private String userProfileImage;
    private String introduction;
    private List<OtherUserPostListResDto> postList;

    public OtherUserResDto(User user, List<OtherUserPostListResDto> postList){
        this.userKey = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickName();
        this.userProfileImage = user.getUserProfileImage();
        this.introduction = user.getIntroduction();
        this.postList = postList;
    }
}
