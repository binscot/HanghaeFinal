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
    private final LevelService levelService;


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
        String userLevel = "lv.1 ???????????????";
        Integer userPoint = 0;

        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(
                    Objects.requireNonNull(bindingResult.getFieldError()
                    ).getDefaultMessage());
        }
        if (!requestDto.getPassword().matches(requestDto.getCheckPassword())){
            throw new EqualPasswordException("??????????????? ???????????? ????????? ???????????? ????????????!");
        }
        if (introduction.length()>300){
            throw new IntroductionLimitException("????????? 300??? ????????? ??????????????????!");
        }

        User user = new User(username, password, nickName, introduction, userProfile, userLevel, userPoint);
        userRepository.save(user);

        Badge firstBadge = new Badge();
        firstBadge.setBadgeName("????????? ???");
        firstBadge.setUser(user);
        badgeRepository.save(firstBadge);

        String createdAt = String.valueOf(user.getCreatedAt());
        String createdDate = createdAt.substring(8,10);
        if (createdDate.equals("09")){
            Badge alphaBadge = new Badge();
            alphaBadge.setBadgeName("???????????????");
            alphaBadge.setUser(user);
            badgeRepository.save(alphaBadge);
        }
        return true;
    }

    //????????? ????????????
    public Boolean checkId(SignupRequestDto requestDto) {
        Optional<User> user = userRepository.findByUsername(requestDto.getUsername());
        if (user.isPresent()) {
            throw new IdDuplicationException("????????? ????????? ID ??? ???????????????.");
        }
        return true;
    }

    //????????? ????????????
    public Boolean checkNick(SignupRequestDto requestDto) {
        Optional<User> foundNickName = userRepository.findByNickName(requestDto.getNickName());
        if (foundNickName.isPresent()){
            throw new NickDuplicationException("????????? ????????? ???????????? ???????????????.");
        }
        return true;
    }


    //????????? ?????????
    public ResponseEntity<LoginResponseDto> login(
            LoginRequestDto loginRequestDto,
            HttpServletResponse response
    ) {

        User user = userRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new UserNotFoundException("???????????? ?????? ID ?????????."));


        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new EqualPasswordException("??????????????? ?????? ????????? ?????????.");
        }

        //????????? ?????? ??????
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
                //????????? ???????????? ?????? ?????? ?????? ?????? ?????? ?????????
                dateList.add(userAttendanceCheck.getDate());
            } else if (userAttendanceCheck.getDate() - (dateList.get(dateList.size()-1))==1){
                dateList.add(userAttendanceCheck.getDate());
            }
        }

        if (dateList.size()==7){
            Badge badge = new Badge();
            badge.setBadgeName("?????????");
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
                .orElseThrow(() -> new UserNotFoundException("???????????? ?????? ID ?????????."));
        String token = jwtTokenProvider.createToken(user.getUsername());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);

        return headers;
    }



    //???????????? ??????
    public UserInfoResponseDto userInfo(UserDetailsImpl userDetails) {
        if (userDetails==null){
            throw new UserNotFoundException("??????????????? ????????????!");
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

        levelService.LevelCheck(user);

        return new UserInfoResponseDto(
                user.getId(),
                user.getUsername(),
                user.getNickName(),
                user.isAlarmRead(),
                user.getUserProfileImage(),
                user.getIntroduction(),
                bookmarkInfoResponseDtoList,
                badgeResponseDtoList,
                user.getPoint(),
                user.getLevel()
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


    //?????? ??? ??? ?????? ??????
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

    //?????? ??????
    @Transactional
    public void removeUser(DeleteUserRequestDto requestDto, UserDetailsImpl userDetails) {
        if (userDetails==null){
            throw new UserNotFoundException("??????????????? ????????????!");
        }
        String password = requestDto.getPassword();
        User user = userDetails.getUser();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordCheckException("??????????????? ?????? ????????? ?????????!");
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
                () -> new UserNotFoundException("???????????? ?????? ID ?????????.")
                );
        List<Post> postList = postRepository.findAllByUser(user);
        for (Post post : postList){
            post.updateUser(anonymousUser);
        }
        List<Paragraph> paragraphList = paragraphRepository.findAllByUser(user);
        for (Paragraph paragraph:paragraphList){
            paragraph.updateUser(anonymousUser);
        }
        userRepository.delete(user);
    }


    //?????? ????????? ????????? ??????
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

    //???????????? ??????
    @Transactional
    public Boolean updatePassword(PasswordRequestDto requestDto,UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new EqualPasswordException("??????????????? ?????? ????????? ?????????.");
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
        // ????????? OAuth2 ??? ?????? ????????? ????????? ?????? ??????
        Integer userPoint = 0;
        String userLevel = "lv.1 ???????????????";
        KakaoUserInfo userInfo = kakaoOAuth2.getUserInfo(accessToken);
        Long kakaoId = userInfo.getId();
        String nickname = userInfo.getNickname();
        String email = userInfo.getEmail();

        // DB ??? ????????? Kakao Id ??? ????????? ??????
        User kakaoUser = userRepository.findByKakaoId(kakaoId)
                .orElse(null);
        // ????????? ????????? ????????????
        if (kakaoUser == null) {
            // ????????? ???????????? ????????? ???????????? ?????? ????????? ????????? ??????
            User sameEmailUser = null;
            if (email != null) {
                sameEmailUser = userRepository.findByUsername(email).orElse(null);
            }
            if (sameEmailUser != null) {
                kakaoUser = sameEmailUser;
                // ????????? ???????????? ????????? ????????? ????????? ?????? ??????
                // ????????? Id ??? ??????????????? ??????
                kakaoUser.setKakaoId(kakaoId);
                userRepository.save(kakaoUser);
            } else {
                // ????????? ????????? ????????????
                // password = ????????? Id + UUID
                String password = kakaoId + String.valueOf(UUID.randomUUID());
                // ???????????? ?????????
                String encodedPassword = passwordEncoder.encode(password);

                if (email != null) {
                    String userImg = "https://binscot-bucket.s3.ap-northeast-2.amazonaws.com/default/photo.png";
                    kakaoUser = new User(nickname, encodedPassword, email, kakaoId, userImg, userLevel, userPoint);
                } else {
                    String userImg = "https://binscot-bucket.s3.ap-northeast-2.amazonaws.com/default/photo.png";
                    String username = "kakaoUser" + kakaoId;
                    kakaoUser = new User(nickname, encodedPassword,username, kakaoId, userImg, userLevel, userPoint);
                }
                userRepository.save(kakaoUser);

                Badge firstBadge = new Badge();
                firstBadge.setBadgeName("????????? ???");
                firstBadge.setUser(kakaoUser);
                badgeRepository.save(firstBadge);

                String createdAt = String.valueOf(kakaoUser.getCreatedAt());
                String createdDate = createdAt.substring(8,10);

                if (createdDate.equals("28")){
                    Badge badge = new Badge();
                    badge.setBadgeName("???????????????");
                    badge.setUser(kakaoUser);
                    badgeRepository.save(badge);
                }
            }
        }

        // ????????? ???????????? ?????? ????????? ??????
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


