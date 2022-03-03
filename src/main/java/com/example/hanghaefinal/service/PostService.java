package com.example.hanghaefinal.service;

import com.example.hanghaefinal.dto.requestDto.PostRequestDto;
import com.example.hanghaefinal.dto.responseDto.CommentResponseDto;
import com.example.hanghaefinal.dto.responseDto.PostDetailResponseDto;
import com.example.hanghaefinal.dto.responseDto.PostLikesResponseDto;
import com.example.hanghaefinal.dto.responseDto.PostResponseDto;
import com.example.hanghaefinal.model.*;
import com.example.hanghaefinal.repository.CommentLikesRepository;
import com.example.hanghaefinal.repository.CommentRepository;
import com.example.hanghaefinal.repository.PostLikesRepository;
import com.example.hanghaefinal.repository.PostRepository;
import com.example.hanghaefinal.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        List<CommentResponseDto> commentList = commentRepository.findAllByPostIdOrderByModifiedAtDesc(postId).stream()
                .map(comment -> comment.toResponseDto()).collect(Collectors.toList());

        //List<CommentLikes> commentLikesList = commentLikesRepository.findBy
        // postLikes 조회
        //List<PostLikes> postLikesList = postLikesRepository.findAllByPostId(post.getId());
        // postId는 이미 알고 있으니까 totalCnt만 주면된다.
        Long postLikesCnt =  postLikesRepository.countByPost(post);
        // paragraph를 작성한 유저와 좋아요
        // comment를 작성한 유저와 좋아요가 필요하다.

        // limitCnt와 paragraph의 개수가 같으면 complete를 true로 반환해라

        return new PostDetailResponseDto(post, commentList, postLikesCnt);
    }

    // 게시글 최신순 전체 조회
    public List<PostResponseDto> viewPostRecent(){
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        List<Post> posts = postRepository.findAllByOrderByModifiedAtDesc();

        int postLikeCnt = 0;
        for (Post post: posts ) {
            List<PostLikes> postLikesList = postLikesRepository.findAllByPostId(post.getId());
            postLikeCnt = postLikesList.size();

            //PostResponseDto postResponseDto = new PostResponseDto(post);
            PostResponseDto postResponseDto = new PostResponseDto(post, postLikeCnt);
            postResponseDtoList.add(postResponseDto);
        }
        return postResponseDtoList;
    }
}
