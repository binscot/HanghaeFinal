package com.example.hanghaefinal.controller;

import com.example.hanghaefinal.dto.requestDto.*;
import com.example.hanghaefinal.dto.responseDto.*;
import com.example.hanghaefinal.model.Post;
import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.security.UserDetailsImpl;
import com.example.hanghaefinal.service.EmailService;
import com.example.hanghaefinal.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@Api(tags = {"User"})
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    // 회원 가입 요청 처리
//    @ApiOperation(value = "회원가입", notes = "회원가입요청")
//    @PostMapping("/user/signup")
//    public ResponseEntity<User> registerUser(
//            @RequestPart(value = "userInfo") SignupRequestDto requestDto,
//            @RequestPart(value = "userProfile", required = false) MultipartFile multipartFile) throws IOException {
//
//        String userProfile = "";
//        if(!multipartFile.isEmpty()) userProfile = s3Uploader.upload(multipartFile, "static");
//
//        User user = userService.registerUser(requestDto, userProfile);
//        return ResponseEntity.ok(user);
//    }

    // 회원 가입 요청 처리
    @ApiOperation(value = "회원가입", notes = "회원가입요청")
    @PostMapping("/user/signup")
    public ResponseEntity<User> registerUser(
            @ModelAttribute SignupRequestDto requestDto) throws IOException {
        User user = userService.registerUser(requestDto);
        return ResponseEntity.ok(user);
    }

    // ID 중복 체크.
    @ApiOperation(value = "ID 중복 체크", notes = "ID 중복 체크")
    @PostMapping("/user/signup/checkID")
    public ResponseEntity<CheckIdResponseDto> checkId(@RequestBody SignupRequestDto requestDto){
        CheckIdResponseDto checkIdResponseDto = userService.checkId(requestDto);
        return ResponseEntity.ok(checkIdResponseDto);
    }

    //닉네임 중복체크
    @ApiOperation(value = "닉네임 중복 체크", notes = "닉네임 중복 체크")
    @PostMapping("/user/signup/checkNick")
    public ResponseEntity<CheckNickResponseDto> checkNick(@RequestBody SignupRequestDto requestDto){
        CheckNickResponseDto checkNickResponseDto = userService.checkNick(requestDto);
        return ResponseEntity.ok(checkNickResponseDto);
    }

    // 로그인
    @ApiOperation(value = "로그인", notes = "로그인")
    @PostMapping("/user/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto,
                                                  HttpServletResponse response) {
        return userService.login(requestDto, response);
    }


    // 카카오 로그인
    // 프론트엔드에서 처리 후 카카오 토큰을 백으로 넘겨 주어 JWT token, username, userid 반환
    @PostMapping("/login/kakaoLogin")
    public ResponseEntity<LoginResponseDto> loginUser(@RequestBody Map<String, Object> param,
                                                      HttpServletResponse response) {
        return userService.kakaoLogin(param.get("kakaoToken").toString(), response);

    }

    // 유저정보 전달
    @ApiOperation(value = "유저정보 전달.", notes = "유저정보 전달.")
    @PostMapping("/user/myInfo")
    public ResponseEntity<UserInfoResponseDto> userInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserInfoResponseDto userInfoResponseDto = userService.userInfo(userDetails);
        return ResponseEntity.ok(userInfoResponseDto);
    }

    //회원정보 수정
    @ApiOperation(value = "회원정보 수정.", notes = "회원정보 수정.")
    @PutMapping("/user/update")
    public ResponseEntity<UserInfoResponseDto> updateUser(
            @ModelAttribute UserUpdateDto updateDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        UserInfoResponseDto userInfoResponseDto = userService.updateUser(updateDto,userDetails);
        return ResponseEntity.ok(userInfoResponseDto);
    }

    //회원 정보 삭제
    @ApiOperation(value = "회원정보 삭제.", notes = "회원정보 삭제.")
    @DeleteMapping("/user/remove")
    public void removeUser(@RequestBody DeleteUserRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        userService.removeUser(requestDto, userDetails);
    }

    //이메일 인증
    @PostMapping("/mailCheck")
    @ApiOperation(value = "회원 가입시 이메인 인증", notes = "기존사용하고 있는 이메일을 통해 인증")
    public String mailCheck(@RequestBody EmailRequestDto requestDto){
        return emailService.mailCheck(requestDto);
    }




    @GetMapping("/myPostList")
    public List<PostResponseDto> viewMyPost(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.viewMyPost(userDetails);
    }

    //게시글 검색
    @GetMapping("/search")
    public List<Post> search(String keyword){
        return userService.search(keyword);
    }



}
