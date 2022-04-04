package com.example.hanghaefinal.service;

import com.example.hanghaefinal.dto.requestDto.*;
import com.example.hanghaefinal.dto.responseDto.*;
import com.example.hanghaefinal.exception.exception.*;
import com.example.hanghaefinal.kakao.KakaoOAuth2;
import com.example.hanghaefinal.kakao.KakaoUserInfo;
import com.example.hanghaefinal.model.*;
import com.example.hanghaefinal.repository.*;
import com.example.hanghaefinal.security.UserDetailsImpl;
import com.example.hanghaefinal.security.jwt.JwtTokenProvider;
import com.example.hanghaefinal.util.S3Uploader;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
    private final ParagraphRepository paragraphRepository;
    private final AlarmRepository alarmRepository;
    private final ParagraphLikesRepository paragraphLikesRepository;


    @Transactional
    public Boolean registerUser(
            SignupRequestDto requestDto,
            BindingResult bindingResult
    ) throws IOException {

        MultipartFile multipartFile = requestDto.getUserProfile();
        String userProfile = "https://binscot-bucket.s3.ap-northeast-2.amazonaws.com/default/photo.png";
        if (!Objects.equals(multipartFile.getOriginalFilename(), "foo.txt"))
            userProfile = s3Uploader.upload(multipartFile, "user");

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
            throw new EqualPasswordException("비밀번호가 비밀번호 확인과 일치하지 않습니다!");
        }
        if (introduction.length()>300){
            throw new IntroductionLimitException("소개는 300자 이하로 작성해주세요!");
        }

        User user = new User(username, password, nickName, introduction, userProfile);
        userRepository.save(user);

        Badge firstBadge = new Badge();
        firstBadge.setBadgeName("시작이 반");
        firstBadge.setUser(user);
        badgeRepository.save(firstBadge);

        String createdAt = String.valueOf(user.getCreatedAt());
        String createdDate = createdAt.substring(8,10);
        if (createdDate.equals("09")){
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
            throw new IdDuplicationException("중복된 사용자 ID 가 존재합니다.");
        }
        return true;
    }

    //닉네임 중복확인
    public Boolean checkNick(SignupRequestDto requestDto) {
        Optional<User> foundNickName = userRepository.findByNickName(requestDto.getNickName());
        if (foundNickName.isPresent()){
            throw new NickDuplicationException("중복된 사용자 닉네임이 존재합니다.");
        }
        return true;
    }


    //로그인 서비스
    public ResponseEntity<LoginResponseDto> login(
            LoginRequestDto loginRequestDto,
            HttpServletResponse response
    ) {

        User user = userRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 ID 입니다."));


        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new EqualPasswordException("비밀번호를 다시 확인해 주세요.");
        }

        //개근상 뱃지 로직
        LocalDateTime localDateTime = LocalDateTime.now();
        String YY = String.valueOf(localDateTime).substring(0,4);
        String MM = String.valueOf(localDateTime).substring(5,7);
        String DD = String.valueOf(localDateTime).substring(8,10);
        String YYMMDD = YY+MM+DD;


        AttendanceCheck attendanceCheck = new AttendanceCheck();
        attendanceCheck.setDate(Integer.parseInt(YYMMDD));
        attendanceCheck.setUser(user);


        attendanceCheckRepository.save(attendanceCheck);
        List<AttendanceCheck> attendanceCheckList = attendanceCheckRepository.findAllByUser(user);
        List<Integer> dateList = new ArrayList<>();
        for (AttendanceCheck userAttendanceCheck: attendanceCheckList){
            if (dateList.size()==0){
                //오픈날 정해지면 오픈 일자 확인 로직 추가 해야함
                dateList.add(userAttendanceCheck.getDate());
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
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 ID 입니다."));
        String token = jwtTokenProvider.createToken(user.getUsername());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);

        return headers;
    }



    //유저정보 전달
    public UserInfoResponseDto userInfo(UserDetailsImpl userDetails) {
        if (userDetails==null){
            throw new UserNotFoundException("유저정보가 없습니다!");
        }
        User user = userDetails.getUser();
        List<Bookmark> bookmarkList = bookmarkRepository.findAllByUser(user);
        List<BookmarkInfoResponseDto> bookmarkInfoResponseDtoList = new ArrayList<>();
        for (Bookmark bookmark:bookmarkList){
            BookmarkInfoResponseDto bookmarkInfoResponseDto = new BookmarkInfoResponseDto(
                    bookmark.getId(),
                    bookmark.getPost().getId(),
                    bookmark.getUser().getId()
            );
            bookmarkInfoResponseDtoList.add(bookmarkInfoResponseDto);
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
                user.isAlarmRead(),
                user.getUserProfileImage(),
                user.getIntroduction(),
                bookmarkInfoResponseDtoList,
                badgeResponseDtoList
        );
    }


    @Transactional
    public UserInfoResponseDto updateUserProfile(MultipartFile file, UserDetailsImpl userDetails) throws IOException {
        String userProfile = "https://binscot-bucket.s3.ap-northeast-2.amazonaws.com/default/photo.png";
        if (!Objects.equals(file.getOriginalFilename(), "foo.txt")){
            userProfile = s3Uploader.upload(file, "user");
        }

        User user = userDetails.getUser();
        user.updateUser(userProfile);
        userRepository.save(user);
        return new UserInfoResponseDto(
                user.getId(),
                user.getUsername(),
                user.getNickName(),
                user.getUserProfileImage(),
                user.getIntroduction()
        );
    }


    @Transactional
    public UserInfoResponseDto updateUser(UserUpdateDto updateDto,UserDetailsImpl userDetails) {

        User user = userDetails.getUser();
        String nickName = updateDto.getNickName();
        String introduction = updateDto.getIntroduction();
        user.updateUser(nickName,introduction);
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

            List<Comment> commentList = commentRepository.findAllByPostIdOrderByModifiedAt(post.getId());
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
            throw new UserNotFoundException("유저정보가 없습니다!");
        }
        String password = requestDto.getPassword();
        User user = userDetails.getUser();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordCheckException("비밀번호를 다시 확인해 주세요!");
        }
        badgeRepository.deleteAllByUser(user);

        attendanceCheckRepository.deleteAllByUser(user);
        bookmarkRepository.deleteAllByUser(user);
        commentRepository.deleteAllByUser(user);
        commentLikesRepository.deleteAllByUser(user);
        paragraphLikesRepository.deleteAllByUser(user);
        postLikesRepository.deleteAllByUser(user);
        alarmRepository.deleteAllByUserId(user.getId());

        User anonymousUser = userRepository.findByUsername("wewrite06@gmail.com").orElseThrow(
                () -> new UserNotFoundException("존재하지 않는 ID 입니다.")
                );
        List<Post> postList = postRepository.findAllByUser(user);
        for (Post post : postList){
            post.updateUser(anonymousUser);
        }
        List<Paragraph> paragraphList = paragraphRepository.findAllByUser(user);
        for (Paragraph paragraph:paragraphList){
//            paragraph.setUser(null);
//            paragraphRepository.save(paragraph);
            paragraph.updateUser(anonymousUser);
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

            List<Comment> commentList = commentRepository.findAllByPostIdOrderByModifiedAt(post.getId());
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
    public Boolean updatePassword(PasswordRequestDto requestDto,UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new EqualPasswordException("비밀번호를 다시 확인해 주세요.");
        }
        String password = passwordEncoder.encode(requestDto.getNewPassword());
        user.updateUserPassword(password);
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
        User kakaoUser = userRepository.findByKakaoId(kakaoId)
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
                    String userImg = "https://binscot-bucket.s3.ap-northeast-2.amazonaws.com/default/photo.png";
                    kakaoUser = new User(nickname, encodedPassword, email, kakaoId, userImg);
                } else {
                    String userImg = "https://binscot-bucket.s3.ap-northeast-2.amazonaws.com/default/photo.png";
                    String username = "kakaoUser" + kakaoId;
                    kakaoUser = new User(nickname, encodedPassword,username, kakaoId, userImg);
                }
                userRepository.save(kakaoUser);

                Badge firstBadge = new Badge();
                firstBadge.setBadgeName("시작이 반");
                firstBadge.setUser(kakaoUser);
                badgeRepository.save(firstBadge);

                String createdAt = String.valueOf(kakaoUser.getCreatedAt());
                String createdDate = createdAt.substring(8,10);

                if (createdDate.equals("28")){
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


