package com.example.hanghaefinal.service;

import com.example.hanghaefinal.dto.requestDto.LoginRequestDto;
import com.example.hanghaefinal.dto.requestDto.SignupRequestDto;
import com.example.hanghaefinal.dto.responseDto.CheckIdResponseDto;
import com.example.hanghaefinal.dto.responseDto.LoginResponseDto;
import com.example.hanghaefinal.dto.responseDto.UserInfoResponseDto;
import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.model.UserRoleEnum;
import com.example.hanghaefinal.repository.UserRepository;
import com.example.hanghaefinal.security.JwtTokenProvider;
import com.example.hanghaefinal.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private static final String ADMIN_TOKEN = "AAABnv/xRVklrnYxKZ0aHgTBcXukeZygoC";

    @Transactional
    public User registerUser(SignupRequestDto requestDto) {

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

// 패스워드 암호화
        String password = passwordEncoder.encode(requestDto.getPassword());

// 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDto.isAdmin()) {
            if (!requestDto.getAdminToken().equals(ADMIN_TOKEN)) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }

        User user = new User(username, password, role);
        return userRepository.save(user);
    }


    //중복확인 서비스
    public CheckIdResponseDto checkId(SignupRequestDto requestDto) {
        CheckIdResponseDto checkIdResponseDto = new CheckIdResponseDto();
        Optional<User> member = userRepository.findByUsername(requestDto.getUsername());
        if (member.isPresent()) {
            checkIdResponseDto.setOk(false);
            checkIdResponseDto.setMsg("중복된 ID가 존재합니다.");
        } else {
            checkIdResponseDto.setOk(true);
            checkIdResponseDto.setMsg("사용 가능한 ID 입니다.");
        }
        return checkIdResponseDto;
    }


    //로그인 서비스
    //존재하지 않거나 비밀번호가 맞지 않을시 오류를 내주고 그렇지 않을경우 토큰을 발행합니다.
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        {
            User member = userRepository.findByUsername(loginRequestDto.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ID 입니다."));
            if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
                throw new IllegalArgumentException("비밀번호를 다시 확인해 주세요.");
            }
            loginResponseDto.setToken(jwtTokenProvider.createToken(member.getUsername()));
            loginResponseDto.setUsername(member.getUsername());
            return loginResponseDto;
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
