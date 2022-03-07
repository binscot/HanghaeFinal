package com.example.hanghaefinal.service;

import com.example.hanghaefinal.dto.requestDto.*;
import com.example.hanghaefinal.dto.responseDto.*;
import com.example.hanghaefinal.kakao.KakaoOAuth2;
import com.example.hanghaefinal.kakao.KakaoUserInfo;
import com.example.hanghaefinal.model.*;
import com.example.hanghaefinal.repository.*;
import com.example.hanghaefinal.security.UserDetailsImpl;
import com.example.hanghaefinal.security.jwt.JwtTokenProvider;
import com.example.hanghaefinal.util.S3Uploader;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

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
    private final BadgeRepository badgeRepository;
    private final AttendanceCheckRepository attendanceCheckRepository;

    @Transactional
    public Boolean registerUser(
            SignupRequestDto requestDto,
            BindingResult bindingResult
    ) throws IOException {

        MultipartFile multipartFile = requestDto.getUserProfile();
        String userProfile = "https://binscot-bucket.s3.ap-northeast-2.amazonaws.com/default/photo.png";
        if (!Objects.equals(multipartFile.getOriginalFilename(), "foo.txt"))
            userProfile = s3Uploader.upload(multipartFile, "static");

        String username = requestDto.getUsername();
        String nickName = requestDto.getNickName();
        String introduction = requestDto.getIntroduction();
        String password = passwordEncoder.encode(requestDto.getPassword());

        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(
                    Objects.requireNonNull(bindingResult.getFieldError()
                    ).getDefaultMessage());
        }
        if (!requestDto.getPassword().matches(requestDto.getCheckPassword())){
            throw new IllegalArgumentException("비밀번호가 비밀번호 확인과 일치하지 않습니다!");
        }
        if (introduction.length()>300){
            throw new IllegalArgumentException("소개는 300자 이하로 작성해주세요!");
        }

        User user = new User(username, password, nickName, introduction, userProfile);
        userRepository.save(user);

        Badge firstBadge = new Badge();
        firstBadge.setBadgeName("시작이 반");
        firstBadge.setUser(user);
        badgeRepository.save(firstBadge);

        String createdAt = String.valueOf(user.getCreatedAt());
        String createdDate = createdAt.substring(8,10);
        if (createdDate.equals("07")){
            Badge alphaBadge = new Badge();
            alphaBadge.setBadgeName("알파테스터");
            alphaBadge.setUser(user);
            badgeRepository.save(alphaBadge);
        }
        return true;
    }

    //아이디 중복확인
    public Boolean checkId(SignupRequestDto requestDto) {
        Optional<User> user = userRepository.findByUsername(requestDto.getUsername());
        if (user.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자 ID 가 존재합니다.");
        }
        return true;
    }

    //닉네임 중복확인
    public Boolean checkNick(SignupRequestDto requestDto) {
        Optional<User> foundNickName = userRepository.findByNickName(requestDto.getNickName());
        if (foundNickName.isPresent()){
            throw new IllegalArgumentException("중복된 사용자 닉네임이 존재합니다.");
        }
        return true;
    }


    //로그인 서비스
    public ResponseEntity<LoginResponseDto> login(
            LoginRequestDto loginRequestDto,
            HttpServletResponse response

    ) {

        User user = userRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ID 입니다."));


        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호를 다시 확인해 주세요.");
        }

        LocalDateTime localDateTime = LocalDateTime.now();
        String YY = String.valueOf(localDateTime).substring(0,4);
        String MM = String.valueOf(localDateTime).substring(5,7);
        String DD = String.valueOf(localDateTime).substring(8,10);
        String YYMMDD = YY+MM+DD;

        //개근상 뱃지 로직
        AttendanceCheck attendanceCheck = new AttendanceCheck();
        attendanceCheck.setDate(Integer.parseInt(YYMMDD));
        attendanceCheck.setUser(user);
        Optional<AttendanceCheck> foundDate = attendanceCheckRepository.findByDate(attendanceCheck.getDate());
        if (!foundDate.isPresent()){
            attendanceCheckRepository.save(attendanceCheck);
        }
        List<AttendanceCheck> attendanceCheckList = attendanceCheckRepository.findAllByUser(user);
        List<Integer> dateList = new ArrayList<>();
        for (AttendanceCheck userAttendanceCheck: attendanceCheckList){
            if (dateList.size()==0){
                //오픈날 정해지면 오픈 일자 확인 로직 추가 해야함
                dateList.add(userAttendanceCheck.getDate());
                System.out.println(dateList.size());
                System.out.println(dateList.get(0));
            } else if (userAttendanceCheck.getDate() - (dateList.get(dateList.size()-1))==1){
                dateList.add(userAttendanceCheck.getDate());
            }
        }

        if (dateList.size()==7){
            Badge badge = new Badge();
            badge.setBadgeName("개근상");
            badge.setUser(user);
            badgeRepository.save(badge);
        }


        LoginResponseDto loginResponseDto = new LoginResponseDto(
                user.getId(),
                user.getUsername(),
                user.getNickName(),
                user.getUserProfileImage(),
                user.getIntroduction());

        String token = jwtTokenProvider.createToken(user.getUsername());

        response.addHeader("Authorization", token);
        return ResponseEntity.ok(loginResponseDto);
    }

    public HttpHeaders tokenToHeader(LoginRequestDto requestDto) {
        User user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ID 입니다."));
        String token = jwtTokenProvider.createToken(user.getUsername());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);

        return headers;
    }



    //유저정보 전달
    public UserInfoResponseDto userInfo(UserDetailsImpl userDetails) {
        if (userDetails==null){
            throw new NullPointerException("유저정보가 없습니다!");
        }
        User user = userDetails.getUser();
        List<Bookmark> bookmarkList = bookmarkRepository.findAllByUser(user);
        List<BookmarkResponseDto> bookmarkResponseDtoList = new ArrayList<>();
        for (Bookmark bookmark:bookmarkList){
            BookmarkResponseDto bookmarkResponseDto = new BookmarkResponseDto(
                    bookmark.getId(),
                    bookmark.getPost().getId(),
                    bookmark.getUser().getId()
            );
            bookmarkResponseDtoList.add(bookmarkResponseDto);
        }

        List<Badge> badgeList = badgeRepository.findAllByUser(user);
        List<BadgeResponseDto> badgeResponseDtoList = new ArrayList<>();
        for (Badge badge:badgeList){
            BadgeResponseDto badgeResponseDto = new BadgeResponseDto(badge.getBadgeName());
            badgeResponseDtoList.add(badgeResponseDto);
        }

        return new UserInfoResponseDto(
                user.getId(),
                user.getUsername(),
                user.getNickName(),
                user.getUserProfileImage(),
                user.getIntroduction(),
                bookmarkResponseDtoList,
                badgeResponseDtoList
        );
    }


    //유저 정보 변경
    @Transactional
    public UserInfoResponseDto updateUser(UserUpdateDto updateDto,UserDetailsImpl userDetails) throws IOException {

        MultipartFile multipartFile = updateDto.getUserProfile();
        String userProfile = "https://binscot-bucket.s3.ap-northeast-2.amazonaws.com/default/photo.png";
        if (!Objects.equals(multipartFile.getOriginalFilename(), "foo.txt"))
            userProfile = s3Uploader.upload(multipartFile, "static");

        User user = userDetails.getUser();
        String nickName = updateDto.getNickName();
        String password = passwordEncoder.encode(updateDto.getPassword());
        String introduction = updateDto.getIntroduction();
        user.updateUser(nickName,password,introduction,userProfile);
        userRepository.save(user);

        return new UserInfoResponseDto(
                user.getId(),
                user.getUsername(),
                user.getNickName(),
                user.getUserProfileImage(),
                user.getIntroduction()
        );
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

            for (Comment comment:commentList ) {
                Long commentLikesCnt = commentLikesRepository.countByComment(comment);
                commentResDtoList.add(new CommentResponseDto(comment, commentLikesCnt));
            }

            PostResponseDto postResponseDto = new PostResponseDto(
                    post,
                    commentResDtoList,
                    postLikeCnt
            );
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


    //내가 작성한 게시글 조회
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

            for (Comment comment:commentList ) {
                Long commentLikesCnt = commentLikesRepository.countByComment(comment);
                commentResDtoList.add(new CommentResponseDto(comment, commentLikesCnt));
            }

            PostResponseDto postResponseDto = new PostResponseDto(
                    post,
                    commentResDtoList,
                    postLikeCnt
            );
            postResponseDtoList.add(postResponseDto);
        }
        return postResponseDtoList;
    }

    //비밀번호 찾기
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

    @Transactional
    public ResponseEntity<LoginResponseDto> kakaoLogin(
            String accessToken,
            HttpServletResponse response
    ) {
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
                // password = 카카오 Id + UUID
                String password = kakaoId + String.valueOf(UUID.randomUUID());
                // 패스워드 인코딩
                String encodedPassword = passwordEncoder.encode(password);

                if (email != null) {
                    kakaoUser = new User(nickname, encodedPassword, email, kakaoId);
                } else {
                    kakaoUser = new User(nickname, encodedPassword, kakaoId);
                }
                userRepository.save(kakaoUser);

                Badge firstBadge = new Badge();
                firstBadge.setBadgeName("시작이반");
                firstBadge.setUser(kakaoUser);
                badgeRepository.save(firstBadge);

                String createdAt = String.valueOf(kakaoUser.getCreatedAt());
                String createdDate = createdAt.substring(8,10);

                if (createdDate.equals("07")){
                    Badge badge = new Badge();
                    badge.setBadgeName("알파테스터");
                    badge.setUser(kakaoUser);
                    badgeRepository.save(badge);
                }
            }
        }

        // 스프링 시큐리티 통해 로그인 처리
        UserDetailsImpl userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("token", jwtTokenProvider.createToken(userDetails.getUser().getUsername()));

        LoginResponseDto loginResponseDto = new LoginResponseDto(
                kakaoUser.getId(),
                kakaoUser.getUsername(),
                kakaoUser.getNickName(),
                kakaoUser.getUserProfileImage(),
                kakaoUser.getIntroduction()
        );

        String tokenString = jsonObj.toString();
        String token = tokenString.substring(10,tokenString.length()-2);
        response.addHeader("Authorization", token);
        return ResponseEntity.ok(loginResponseDto);
    }
}


