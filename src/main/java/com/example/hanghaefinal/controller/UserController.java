package com.example.hanghaefinal.controller;

import com.example.hanghaefinal.dto.requestDto.*;
import com.example.hanghaefinal.dto.responseDto.*;
import com.example.hanghaefinal.security.UserDetailsImpl;
import com.example.hanghaefinal.service.EmailService;
import com.example.hanghaefinal.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "https://main.d2l6bnge3hnh7g.amplifyapp.com")
@RequiredArgsConstructor
@Api(tags = {"User"})
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    // 회원 가입 요청 처리
    @ApiOperation(value = "회원가입", notes = "회원가입요청")
    @PostMapping("/user/signup")
    public ResponseEntity<Boolean> registerUser(
            @Validated
            @ModelAttribute SignupRequestDto requestDto,
            BindingResult bindingResult) throws IOException {
        return ResponseEntity.ok(userService.registerUser(requestDto,bindingResult));
    }

    // ID 중복 체크.
    @ApiOperation(value = "ID 중복 체크", notes = "ID 중복 체크")
    @PostMapping("/user/signup/checkID")
    public ResponseEntity<Boolean> checkId(@RequestBody SignupRequestDto requestDto){
        return ResponseEntity.ok(userService.checkId(requestDto));
    }

    //닉네임 중복체크
    @ApiOperation(value = "닉네임 중복 체크", notes = "닉네임 중복 체크")
    @PostMapping("/user/signup/checkNick")
    public ResponseEntity<Boolean> checkNick(@RequestBody SignupRequestDto requestDto){
        return ResponseEntity.ok(userService.checkNick(requestDto));
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
//    @ApiOperation(value = "회원정보 수정.", notes = "회원정보 수정.")
//    @PutMapping("/user/update")
//    public ResponseEntity<UserInfoResponseDto> updateUser(
//            @ModelAttribute UserUpdateDto updateDto,
//            @AuthenticationPrincipal UserDetailsImpl userDetails
//    ) throws IOException {
//        UserInfoResponseDto userInfoResponseDto = userService.updateUser(updateDto,userDetails);
//        return ResponseEntity.ok(userInfoResponseDto);
//    }

    @ApiOperation(value = "회원정보 수정.", notes = "회원정보 수정.")
    @PutMapping("/user/update")
    public ResponseEntity<UserInfoResponseDto> updateUser(
            @RequestBody UserUpdateDto updateDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        UserInfoResponseDto userInfoResponseDto = userService.updateUser(updateDto,userDetails);
        return ResponseEntity.ok(userInfoResponseDto);
    }

    //유저 프로필 수정
//    @ApiOperation(value = "회원정보 수정.", notes = "회원정보 수정.")
//    @PutMapping("/user/updateProfile")
//    public ResponseEntity<UserInfoResponseDto> updateUserProfile(
//            @RequestPart(value = "userProfile", required = false) MultipartFile multipartFile,
//            @AuthenticationPrincipal UserDetailsImpl userDetails
//    ) throws IOException {
//        UserInfoResponseDto userInfoResponseDto = userService.updateUserProfile(multipartFile,userDetails);
//        return ResponseEntity.ok(userInfoResponseDto);
//    }

    //유저 사진 수정
    @ApiOperation(value = "회원정보 수정.", notes = "회원정보 수정.")
    @PutMapping("/user/updateProfile")
    public ResponseEntity<UserInfoResponseDto> updateUserProfile(
            @RequestParam("userProfile") MultipartFile userProfile,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        UserInfoResponseDto userInfoResponseDto = userService.updateUserProfile(userProfile, userDetails);
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
    public ResponseEntity<MailKeyResponseDto> mailCheck(@RequestBody EmailRequestDto requestDto){
        MailKeyResponseDto mailKeyResponseDto = emailService.mailCheck(requestDto);
        return ResponseEntity.ok(mailKeyResponseDto);
    }

    //내가 쓴 게시물 검색
    @GetMapping("/myPostList")
    public ResponseEntity<List<PostResponseDto>> viewMyPost(@AuthenticationPrincipal UserDetailsImpl userDetails){
        List<PostResponseDto> postResponseDtoList = userService.viewMyPost(userDetails);
        return ResponseEntity.ok(postResponseDtoList);
    }

    //게시글 검색
    @GetMapping("/search")
    public ResponseEntity<List<PostResponseDto>> search(@RequestBody SearchRequestDto requestDto){
        return ResponseEntity.ok(userService.search(requestDto));
    }

    //비밀번호 찾기
    @PutMapping("/findPassword")
    public ResponseEntity<Boolean> updatePassword(@RequestBody PasswordRequestDto requestDto){
        return ResponseEntity.ok(userService.updatePassword(requestDto));
    }

}
