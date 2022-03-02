package com.example.hanghaefinal.service;

import com.example.hanghaefinal.dto.requestDto.LoginRequestDto;
import com.example.hanghaefinal.dto.requestDto.SignupRequestDto;
import com.example.hanghaefinal.dto.responseDto.CheckIdResponseDto;
import com.example.hanghaefinal.dto.responseDto.CheckNickResponseDto;
import com.example.hanghaefinal.dto.responseDto.LoginResponseDto;
import com.example.hanghaefinal.dto.responseDto.UserInfoResponseDto;
import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.repository.UserRepository;
import com.example.hanghaefinal.security.JwtTokenProvider;
import com.example.hanghaefinal.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public User registerUser(SignupRequestDto requestDto, String userProfile) {

        //유효성 체크 추가해야함
        String username = requestDto.getUsername();
        Optional<User> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자 ID 가 존재합니다.");
        }
        if (requestDto.getUsername() == null) {
            throw new NullPointerException("아이디를 입력해주세요");
        }
        if (Objects.equals(requestDto.getUsername(), "")) {
            throw new NullPointerException("아이디를 입력해주세요!!!!!!!!!");
        }
        if (requestDto.getPassword() == null) {
            throw new NullPointerException("비밀번호를 입력해주세요");
        }
        if (Objects.equals(requestDto.getPassword(), "")) {
            throw new NullPointerException("비밀번호를 입력해주세요!!!!!!!!!!!!");
        }
        String nickName = requestDto.getNickName();
        Optional<User> foundNickName = userRepository.findByNickName(nickName);
        if (foundNickName.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자 닉네임이 존재합니다.");
        }
        String introduction = requestDto.getIntroduction();
        if (introduction.length()>300){
            throw new IllegalArgumentException("소개는 300자 이하로 작성해주세요!");
        }
// 패스워드 암호화
        String password = passwordEncoder.encode(requestDto.getPassword());

        User user = new User(username, password, nickName, introduction, userProfile);
        return userRepository.save(user);
    }


    //중복확인 서비스
    public CheckIdResponseDto checkId(SignupRequestDto requestDto) {
        CheckIdResponseDto checkIdResponseDto = new CheckIdResponseDto();
        Optional<User> user = userRepository.findByUsername(requestDto.getUsername());
        if (user.isPresent()) {
            checkIdResponseDto.setOk(false);
            checkIdResponseDto.setMsg("중복된 ID가 존재합니다.");
        } else {
            checkIdResponseDto.setOk(true);
            checkIdResponseDto.setMsg("사용 가능한 ID 입니다.");
        }
        return checkIdResponseDto;
    }

    public CheckNickResponseDto checkNick(SignupRequestDto requestDto) {
        CheckNickResponseDto checkNickResponseDto = new CheckNickResponseDto();
        Optional<User> foundNickName = userRepository.findByNickName(requestDto.getNickName());
        if (foundNickName.isPresent()){
            checkNickResponseDto.setOk(false);
            checkNickResponseDto.setMsg("중복된 닉네임이 존재합니다!");
        }
        if (!foundNickName.isPresent()){
            checkNickResponseDto.setOk(true);
            checkNickResponseDto.setMsg("사용가능한 닉네임입니다!");
        }

        return null;
    }


    //로그인 서비스
    //존재하지 않거나 비밀번호가 맞지 않을시 오류를 내주고 그렇지 않을경우 토큰을 발행합니다.
    public ResponseEntity<LoginResponseDto> login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        {
            User user = userRepository.findByUsername(loginRequestDto.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ID 입니다."));
            if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
                throw new IllegalArgumentException("비밀번호를 다시 확인해 주세요.");
            }
            loginResponseDto.setUserKey(user.getId());
            loginResponseDto.setUsername(user.getUsername());
            loginResponseDto.setNickname(user.getNickName());
            loginResponseDto.setUserProfileImage(user.getUserProfileImage());
            loginResponseDto.setIntroduction(user.getIntroduction());


            String token = jwtTokenProvider.createToken(user.getUsername());
            Cookie cookie = new Cookie("X-AUTH-TOKEN", token);
            cookie.setPath("/");    // 이 경로에 바로 넣어줘야지 모든 경로에서 쿠키를 사용할 수 있다.
            // https에서 setHttpOnly(true) 를 사용하는지 안하는지 검색해보자
            cookie.setHttpOnly(true);   // 프론트에서 헤더에 있는 토큰을 못 꺼내서 쓴다. 애초에 헤더에서 꺼내서 사용하는게 아니라 다른 방식으로 사용하나 보군
            cookie.setSecure(true);     // https 에서 사용한다.
            response.addCookie(cookie); // 이거만 있으면 프론트에서 받을 수 있다.


            return ResponseEntity.ok(loginResponseDto);
        }
    }

    //유저정보 전달
    public UserInfoResponseDto userInfo(UserDetailsImpl userDetails) {
        UserInfoResponseDto userInfoResponseDto = new UserInfoResponseDto();
        User user = userDetails.getUser();
        if (user == null)
            throw new NullPointerException("유저 정보가 없습니다.");
        userInfoResponseDto.setId(user.getId());
        userInfoResponseDto.setUsername(user.getUsername());
        userInfoResponseDto.setIs_login(false);
        return userInfoResponseDto;
    }


}
