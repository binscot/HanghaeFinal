package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class UserInfoResponseDto {
    private Long userKey;
    private String username;
    private String nickname;
    private boolean isAlarmRead;
    private String userProfileImage;
    private String introduction;
    private List<BookmarkInfoResponseDto> bookmarkInfoResponseDtoList;
    private List<BadgeResponseDto> badgeResponseDtoList;
    private String userLevel;
    private Integer userPoint;

    public UserInfoResponseDto(User user){
        this.userKey = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickName();
        this.userProfileImage = user.getUserProfileImage();
        this.introduction = user.getIntroduction();
    }

    public UserInfoResponseDto(Long id, String username, String nickName, boolean bool, String userProfileImage, String introduction, List<BookmarkInfoResponseDto> bookmarkInfoResponseDtoList, List<BadgeResponseDto> badgeResponseDtoList, Integer userPoint, String userLevel) {
        this.userKey=id;
        this.username=username;
        this.nickname=nickName;
        this.isAlarmRead=bool;
        this.userProfileImage=userProfileImage;
        this.introduction=introduction;
        this.bookmarkInfoResponseDtoList=bookmarkInfoResponseDtoList;
        this.badgeResponseDtoList = badgeResponseDtoList;
        this.userPoint = userPoint;
        this.userLevel = userLevel;

    }

    public UserInfoResponseDto(Long id, String username, String nickName, String userProfileImage, String introduction) {
        this.userKey=id;
        this.username=username;
        this.nickname=nickName;
        this.userProfileImage=userProfileImage;
        this.introduction=introduction;
    }

    public UserInfoResponseDto(Long id, String username, String nickName, String introduction) {
        this.userKey=id;
        this.username=username;
        this.nickname=nickName;
        this.introduction=introduction;
    }


}
