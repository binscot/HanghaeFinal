package com.example.hanghaefinal.service;

import com.example.hanghaefinal.dto.requestDto.PostRequestDto;
import com.example.hanghaefinal.dto.responseDto.*;
import com.example.hanghaefinal.model.*;
import com.example.hanghaefinal.repository.*;
import com.example.hanghaefinal.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final PostLikesRepository postLikesRepository;
    private final CommentRepository commentRepository;
    private final CommentLikesRepository commentLikesRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    public String uploadImageFile(MultipartFile multipartFile, PostRequestDto requestDto) throws IOException {
        //String originalFileName = multipartFile.getOriginalFilename();
        //String convertedFileName = UUID.randomUUID() + originalFileName;
        //requestDto.setImageUrl(convertedFileName);
        String dirName = "image";
        //s3Uploader.upload(multipartFile, convertedFileName);

        String defaultImg = "https://taeks3bucket.s3.ap-northeast-2.amazonaws.com/image/defaultPhoto.png";
        if (!Objects.equals(multipartFile.getOriginalFilename(), "foo.txt"))
            defaultImg = s3Uploader.upload(multipartFile, "image");
        //String uploadUrl =  s3Uploader.upload(multipartFile, dirName);
        //requestDto.setPostImageUrl(defaultImg);
        //log.info("~~~ uploadUrl : " + uploadUrl );
        //requestDto.setPostImageUrl(uploadUrl);
        return defaultImg;
    }

    public Boolean savePost(PostRequestDto postRequestDto, User user, String defaultImg){
        Post post = new Post(postRequestDto, user, defaultImg);
        postRepository.save(post);

        return true;
    }

    // 게시글 상세조회
    public PostDetailResponseDto viewPostDetail(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("postId가 존재하지 않습니다.")
        );
        // paragraphList 조회
        // List<Paragraph> paragraphList =
        // commentList 조회
        //List<Comment> commentList = commentRepository.findAllByPostIdOrderByModifiedAtDesc(postId);
        // 위처럼 commentList로 조회하면 안된다. Comment안에 user랑 post있고 post안에 user 가 있다.. (궁금하면 다시 해봐)

        // map은 요소들을 특정조건에 해당하는 값으로 변환해 준다 즉 여기서는 jpa 구문으로 가져온걸
        // toResponseDto를 사용해서 CommentResponseDto로 변환해 준다. ( 여기서 (postId) 까지만 작성했으면 타입이 안맞는다 )
        /*List<CommentResponseDto> commentList = commentRepository.findAllByPostIdOrderByModifiedAtDesc(postId).stream()
                .map(comment -> comment.toResponseDto()).collect(Collectors.toList());*/

        List<Comment> commentList2 = commentRepository.findAllByPostIdOrderByModifiedAtDesc(postId);
        List<CommentResponseDto> commentResDtoList = new ArrayList<>();

        // List<Comment>를 각각 List<CommentResponseDto> 에 담는다
        for (Comment comment:commentList2 ) {
            Long commentLikesCnt = commentLikesRepository.countByComment(comment);
            commentResDtoList.add(new CommentResponseDto(comment, commentLikesCnt));
        }

        //List<CommentLikes> commentLikesList = commentLikesRepository.findBy
        // postLikes 조회
        //List<PostLikes> postLikesList = postLikesRepository.findAllByPostId(post.getId());
        // postId는 이미 알고 있으니까 totalCnt만 주면된다.
        Long postLikesCnt =  postLikesRepository.countByPost(post);
        // paragraph를 작성한 유저와 좋아요
        // comment를 작성한 유저와 좋아요가 필요하다.

        // limitCnt와 paragraph의 개수가 같으면 complete를 true로 반환해라
        return new PostDetailResponseDto(post, commentResDtoList, postLikesCnt);
//        return new PostDetailResponseDto(post, commentList, postLikesCnt);
    }

    // 완성작 게시글 전체 조회 - 최신순
    public List<PostResponseDto> viewPostRecent(){
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        List<Post> posts = postRepository.findAllByOrderByModifiedAtDesc();

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

    // 완성작 게시글 전체 조회 - 추천순(좋아요순)
    public List<PostResponseDto> viewPostRecommend(){
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        List<Post> posts = postRepository.findAllByOrderByModifiedAtDesc();

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

            PostResponseDto postResponseDto = new PostResponseDto(post, commentResDtoList, postLikeCnt);
            //postResponseDto.getPostLikesCnt();
            postResponseDtoList.add(postResponseDto);
        }

        // post좋아요 순으로 내림차순 정렬(좋아요 많은게 위에 보이게끔)
        Comparator<PostResponseDto> comparator = Comparator.comparing(PostResponseDto::getPostLikesCnt, Comparator.reverseOrder());
        List<PostResponseDto> responseDtoList = postResponseDtoList.stream().sorted(comparator).collect(Collectors.toList());
        // 결과 출력
        /*postResponseDtoList.stream().sorted(comparator)
                .forEach(o -> {
                    System.out.println("~~~ o.getPostLikesCnt() : " + o.getPostLikesCnt());
                });
        System.out.println("-----------------------------------------------");*/

        return responseDtoList;
        //return postResponseDtoList;
    }

    // 미완성 게시글 전체 조회 - 최신순
    public List<PostResponseDto> viewPostIncomplete(){
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        List<Post> posts = postRepository.findAllByOrderByModifiedAtDesc();

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

            PostResponseDto postResponseDto = new PostResponseDto(post, commentResDtoList, postLikeCnt);
            postResponseDtoList.add(postResponseDto);
        }
        return postResponseDtoList;
    }

    // 다른 유저 페이지
    public OtherUserResDto viewUserPage(Long userKey){
        User user = userRepository.findById(userKey).orElseThrow(
                () -> new IllegalArgumentException("해당 유저가 존재하지 않습니다.")
        );

        List<Post> postList = postRepository.findAllByUserIdOrderByModifiedAtDesc(userKey);
        List<OtherUserPostListResDto> otherUserList = new ArrayList<>();

        for (Post post: postList ) {
            otherUserList.add(new OtherUserPostListResDto(post));
        }

        //OtherUserResDto otherUserResDto = new OtherUserResDto(user, postList);
        return new OtherUserResDto(user, otherUserList);
    }

}