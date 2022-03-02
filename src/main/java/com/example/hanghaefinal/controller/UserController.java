package com.example.hanghaefinal.controller;

import com.example.hanghaefinal.dto.requestDto.LoginRequestDto;
import com.example.hanghaefinal.dto.requestDto.SignupRequestDto;
import com.example.hanghaefinal.dto.requestDto.UserUpdateDto;
import com.example.hanghaefinal.dto.responseDto.CheckIdResponseDto;
import com.example.hanghaefinal.dto.responseDto.CheckNickResponseDto;
import com.example.hanghaefinal.dto.responseDto.LoginResponseDto;
import com.example.hanghaefinal.dto.responseDto.UserInfoResponseDto;
import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.security.UserDetailsImpl;
import com.example.hanghaefinal.service.UserService;
import com.example.hanghaefinal.util.S3Uploader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@Api(tags = {"User"})
public class UserController {

    private final UserService userService;
    private final S3Uploader s3Uploader;

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

    @ApiOperation(value = "ID 중복 체크", notes = "ID 중복 체크")
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
            @RequestPart(value = "userInfo") UserUpdateDto updateDto,
            @RequestPart(value = "userProfile", required = false) MultipartFile multipartFile,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException{
        String userProfile = "";
        if(!multipartFile.isEmpty()) userProfile = s3Uploader.upload(multipartFile, "static");
        UserInfoResponseDto userInfoResponseDto = userService.updateUser(updateDto,userDetails,userProfile);
        return ResponseEntity.ok(userInfoResponseDto);
    }

}
