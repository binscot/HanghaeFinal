package com.example.hanghaefinal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Setter
@Getter // get 함수를 일괄적으로 만들어줍니다.
@NoArgsConstructor // 기본 생성자를 만들어줍니다.
@Entity // DB 테이블 역할을 합니다.
@AllArgsConstructor
@Builder
public class User extends Timestamped{

    // ID가 자동으로 생성 및 증가합니다.
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    // nullable: null 허용 여부
    // unique: 중복 허용 여부 (false 일때 중복 허용)
    @Column(nullable = false, unique = true)
    private String username;

    @JsonIgnore // pw json에서 숨김처리
    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String nickName;

    @Column(nullable = true)
    private boolean isAlarmRead;

    @Column(nullable = true)
    private String userProfileImage;

    @Column(nullable = true)
    private String introduction;

    @Column
    private Long kakaoId;

    @Column
    private String level;

    @Column
    private Integer point;



    public User(String username, String password, String nickName, String introduction, String userProfile) {
        this.username = username;
        this.password = password;
        this.nickName = nickName;
        this.introduction = introduction;
        this.userProfileImage = userProfile;
        this.kakaoId = null;
    }

    public User(String username, String encodedPassword, String email, Long kakaoId, String userProfileImage) {
        this.username=email;
        this.password=encodedPassword;
        this.nickName=username;
        this.userProfileImage = userProfileImage;
        this.kakaoId=kakaoId;

    }

//    public User(String username, String nickName, String encodedPassword, Long kakaoId) {
//        this.username=username;
//        this.nickName=nickName;
//        this.password=encodedPassword;
//        this.kakaoId=kakaoId;
//    }


//    public void updateUser(String nickName, String password, String introduction, String userProfile) {
//        this.nickName=nickName;
//        this.password=password;
//        this.introduction=introduction;
//        this.userProfileImage=userProfile;
//    }

//    public void updateUser(String password) {
//        this.password=password;
//    }

    public void updateUser(String userProfile) {
        this.userProfileImage=userProfile;
    }


    public void updateUser(String nickName,  String introduction) {
        this.nickName=nickName;
        this.introduction=introduction;
    }

    public void updateUserAlaram(boolean bool){
        this.isAlarmRead = bool;
    }


    public void updateUserPassword(String password) {
        this.password=password;
    }
}
