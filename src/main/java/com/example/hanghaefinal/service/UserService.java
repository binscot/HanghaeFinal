package com.example.hanghaefinal.service;

import com.example.hanghaefinal.dto.requestDto.*;
import com.example.hanghaefinal.dto.responseDto.*;
import com.example.hanghaefinal.kakao.KakaoOAuth2;
import com.example.hanghaefinal.kakao.KakaoUserInfo;
import com.example.hanghaefinal.model.*;
import com.example.hanghaefinal.repository.*;
import com.example.hanghaefinal.security.JwtTokenProvider;
import com.example.hanghaefinal.security.UserDetailsImpl;
import com.example.hanghaefinal.util.S3Uploader;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final S3Uploader s3Uploader;
    private final PostRepository postRepository;
    private final BookmarkRepository bookmarkRepository;
    private final KakaoOAuth2 kakaoOAuth2;
    private final PostLikesRepository postLikesRepository;
    private final CommentRepository commentRepository;
    private final CommentLikesRepository commentLikesRepository;

    @Transactional
    public Boolean registerUser(SignupRequestDto requestDto) throws IOException {
        //        if(!multipartFile.isEmpty()) userProfile = s3Uploader.upload(multipartFile, "static");

//        String userProfile = "";
//        if (requestDto.getUserProfile()!=null){
//            MultipartFile multipartFile = requestDto.getUserProfile();
//            userProfile = s3Uploader.upload(multipartFile, "static");
//        } else {
//
//            userProfile = "https://binscot-bucket.s3.ap-northeast-2.amazonaws.com/static/photo.png";
//        }
        MultipartFile multipartFile = requestDto.getUserProfile();
        String userProfile = "https://binscot-bucket.s3.ap-northeast-2.amazonaws.com/default/photo.png";
        if (!Objects.equals(multipartFile.getOriginalFilename(), "foo.txt")) userProfile = s3Uploader.upload(multipartFile, "static");

        //유효성 체크 추가해야함
        String username = requestDto.getUsername();
        Optional<User> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자 ID 가 존재합니다.");
        }
        //To do Validation으로 변경
        if (Objects.equals(requestDto.getUsername(), "")) {
            throw new NullPointerException("아이디를 입력해주세요!");
        }
        if (Objects.equals(requestDto.getPassword(), "")) {
            throw new NullPointerException("비밀번호를 입력해주세요!");
        }
        if (Objects.equals(requestDto.getCheckPassword(), "")) {
            throw new NullPointerException("비밀번호 확인을 입력해주세요!");
        }
        if (!requestDto.getPassword().matches(requestDto.getCheckPassword())){
            throw new IllegalArgumentException("비밀번호가 비밀번호 확인과 일치하지 않습니다!");
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
        userRepository.save(user);
        return true;
    }


    //아이디 중복확인
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

    //닉네임 중복확인
    public CheckNickResponseDto checkNick(SignupRequestDto requestDto) {
        CheckNickResponseDto checkNickResponseDto = new CheckNickResponseDto();
        Optional<User> foundNickName = userRepository.findByNickName(requestDto.getNickName());
        System.out.println(foundNickName.isPresent());
        if (foundNickName.isPresent()){
            checkNickResponseDto.setOk(false);
            checkNickResponseDto.setMsg("중복된 닉네임이 존재합니다!");
        } else {
            checkNickResponseDto.setOk(true);
            checkNickResponseDto.setMsg("사용가능한 닉네임입니다!");
        }
        return checkNickResponseDto;
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
//            Cookie cookie = new Cookie("X-AUTH-TOKEN", token);
//            cookie.setPath("/");    // 이 경로에 바로 넣어줘야지 모든 경로에서 쿠키를 사용할 수 있다.
            // https에서 setHttpOnly(true) 를 사용하는지 안하는지 검색해보자
//            cookie.setHttpOnly(true);   // 프론트에서 헤더에 있는 토큰을 못 꺼내서 쓴다. 애초에 헤더에서 꺼내서 사용하는게 아니라 다른 방식으로 사용하나 보군
//            cookie.setSecure(true);     // https 에서 사용한다.
//            response.addCookie(cookie); // 이거만 있으면 프론트에서 받을 수 있다.

            response.addHeader("X-AUTH-TOKEN", token);
            return ResponseEntity.ok(loginResponseDto);
        }
    }

    @Transactional
    public ResponseEntity<LoginResponseDto> kakaoLogin(String accessToken, HttpServletResponse response) {
        // 카카오 OAuth2 를 통해 카카오 사용자 정보 조회
        KakaoUserInfo userInfo = kakaoOAuth2.getUserInfo(accessToken);
        Long kakaoId = userInfo.getId();
        String nickname = userInfo.getNickname();
        String email = userInfo.getEmail();



        // DB 에 중복된 Kakao Id 가 있는지 확인
        User kakaoUser = userRepository.findByUsername(email)
                .orElse(null);

        // 카카오 정보로 회원가입
        if (kakaoUser == null) {
            // 카카오 이메일과 동일한 이메일을 가진 회원이 있는지 확인
            User sameEmailUser = null;

            if (email != null) {
                sameEmailUser = userRepository.findByUsername(email).orElse(null);
            }
            if (sameEmailUser != null) {
                kakaoUser = sameEmailUser;
                // 카카오 이메일과 동일한 이메일 회원이 있는 경우
                // 카카오 Id 를 회원정보에 저장
                kakaoUser.setKakaoId(kakaoId);
                userRepository.save(kakaoUser);
            } else {
                // 카카오 정보로 회원가입
                // username = 카카오 nickname
                String username = nickname;
                // password = 카카오 Id + ADMIN TOKEN
                String password = kakaoId + String.valueOf(UUID.randomUUID());;
                System.out.println("password:"+ password);
                // 패스워드 인코딩
                String encodedPassword = passwordEncoder.encode(password);



                if (email != null) {
                    kakaoUser = new User(username, encodedPassword, email, kakaoId);
                } else {
                    kakaoUser = new User(username, encodedPassword, kakaoId);
                }
                userRepository.save(kakaoUser);
            }
        }

        // 스프링 시큐리티 통해 로그인 처리
        UserDetailsImpl userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("token", jwtTokenProvider.createToken(userDetails.getUser().getUsername()));


        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setUserKey(kakaoUser.getId());
        loginResponseDto.setUsername(kakaoUser.getUsername());
        loginResponseDto.setNickname(kakaoUser.getNickName());
        loginResponseDto.setUserProfileImage(kakaoUser.getUserProfileImage());
        loginResponseDto.setIntroduction(kakaoUser.getIntroduction());

        String tokenString = jsonObj.toString();

        String token = tokenString.substring(10,tokenString.length()-2);
        response.addHeader("X-AUTH-TOKEN", token);
        return ResponseEntity.ok(loginResponseDto);
    }


    //유저정보 전달
    public UserInfoResponseDto userInfo(UserDetailsImpl userDetails) {
        if (userDetails==null){
            throw new NullPointerException("유저정보가 없습니다!");
        }
        UserInfoResponseDto userInfoResponseDto = new UserInfoResponseDto();
        User user = userDetails.getUser();
        List<Bookmark> bookmarkList = bookmarkRepository.findAllByUser(user);
        List<BookmarkResponseDto> bookmarkResponseDtoList = new ArrayList<>();
        for (Bookmark bookmark:bookmarkList){
            BookmarkResponseDto bookmarkResponseDto = new BookmarkResponseDto(bookmark.getId(),bookmark.getPost().getId(),bookmark.getUser().getId());
            bookmarkResponseDtoList.add(bookmarkResponseDto);
        }
        userInfoResponseDto.setUserKey(user.getId());
        userInfoResponseDto.setUsername(user.getUsername());
        userInfoResponseDto.setNickname(user.getNickName());
        userInfoResponseDto.setUserProfileImage(user.getUserProfileImage());
        userInfoResponseDto.setIntroduction(user.getIntroduction());
        userInfoResponseDto.setBookmarkResponseDtoList(bookmarkResponseDtoList);
        return userInfoResponseDto;
    }


    //유저 정보 변경
    @Transactional
    public UserInfoResponseDto updateUser(UserUpdateDto updateDto,UserDetailsImpl userDetails) throws IOException {
//        String userProfile = "";
//        if(!multipartFile.isEmpty()) userProfile = s3Uploader.upload(multipartFile, "static");
        MultipartFile multipartFile = updateDto.getUserProfile();
        String userProfile = "https://binscot-bucket.s3.ap-northeast-2.amazonaws.com/default/photo.png";
        if (!Objects.equals(multipartFile.getOriginalFilename(), "foo.txt")) userProfile = s3Uploader.upload(multipartFile, "static");
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );

        String nickName = updateDto.getNickName();
        String password = passwordEncoder.encode(updateDto.getPassword());
        String introduction = updateDto.getIntroduction();
        user.updateUser(nickName,password,introduction,userProfile);
        userRepository.save(user);
        UserInfoResponseDto userInfoResponseDto = new UserInfoResponseDto();
        userInfoResponseDto.setUserKey(user.getId());
        userInfoResponseDto.setUsername(user.getUsername());
        userInfoResponseDto.setNickname(user.getNickName());
        userInfoResponseDto.setUserProfileImage(user.getUserProfileImage());
        userInfoResponseDto.setIntroduction(user.getIntroduction());
        return userInfoResponseDto;
    }


    //검색 추 후 옮길 예정
    public List<PostResponseDto> search(SearchRequestDto requestDto) {
        String keyword = requestDto.getKeyword();

        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        List<Post> posts = postRepository.findByTitleContaining(keyword);

        int postLikeCnt = 0;
        for (Post post: posts ) {
            List<PostLikes> postLikesList = postLikesRepository.findAllByPostId(post.getId());
            postLikeCnt = postLikesList.size();

            List<Comment> commentList = commentRepository.findAllByPostIdOrderByModifiedAtDesc(post.getId());
            List<CommentResponseDto> commentResDtoList = new ArrayList<>();

            // List<Comment>를 각각 List<CommentResponseDto> 에 담는다
            for (Comment comment:commentList ) {
                Long commentLikesCnt = commentLikesRepository.countByComment(comment);
                commentResDtoList.add(new CommentResponseDto(comment, commentLikesCnt));
            }

            //PostResponseDto postResponseDto = new PostResponseDto(post);
            PostResponseDto postResponseDto = new PostResponseDto(post, commentResDtoList, postLikeCnt);
            postResponseDtoList.add(postResponseDto);
        }
        return postResponseDtoList;

    }

    //회원 탈퇴
    @Transactional
    public void removeUser(DeleteUserRequestDto requestDto, UserDetailsImpl userDetails) {
        if (userDetails==null){
            throw new NullPointerException("유저정보가 없습니다!");
        }
        String password = requestDto.getPassword();
        User user = userDetails.getUser();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호를 다시 확인해 주세요!");
        }
        userRepository.delete(user);
    }


    public List<PostResponseDto> viewMyPost(UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        List<Post> posts = postRepository.findAllByUserIdOrderByModifiedAtDesc(user.getId());

        int postLikeCnt = 0;
        for (Post post: posts ) {
            List<PostLikes> postLikesList = postLikesRepository.findAllByPostId(post.getId());
            postLikeCnt = postLikesList.size();

            List<Comment> commentList = commentRepository.findAllByPostIdOrderByModifiedAtDesc(post.getId());
            List<CommentResponseDto> commentResDtoList = new ArrayList<>();

            // List<Comment>를 각각 List<CommentResponseDto> 에 담는다
            for (Comment comment:commentList ) {
                Long commentLikesCnt = commentLikesRepository.countByComment(comment);
                commentResDtoList.add(new CommentResponseDto(comment, commentLikesCnt));
            }

            //PostResponseDto postResponseDto = new PostResponseDto(post);
            PostResponseDto postResponseDto = new PostResponseDto(post, commentResDtoList, postLikeCnt);
            postResponseDtoList.add(postResponseDto);
        }
        return postResponseDtoList;

    }

    @Transactional
    public Boolean updatePassword(PasswordRequestDto requestDto) {
        User user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ID 입니다.")
        );
        if (!requestDto.getPassword().matches(requestDto.getCheckPassword())){
            throw new IllegalArgumentException("비밀번호가 비밀번호 확인과 일치하지 않습니다!");
        }
        String password = passwordEncoder.encode(requestDto.getPassword());
        user.updateUser(password);
        userRepository.save(user);
        return true;
    }
}


