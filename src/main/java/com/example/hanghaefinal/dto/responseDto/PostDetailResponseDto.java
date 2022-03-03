package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.Post;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
public class PostDetailResponseDto {
    private Long postKey; // postId
    private String postImageUrl;
    private String color;
    private int limitCnt;
    private boolean complete;
    // ----- 밑에 부터 post컬럼에 있는 값이 아닌 것 -----
    //private double postScore;
    //private List<CommentResponseDto> commnetList;
    private Long postLikesCnt;
    private List<CommentResponseDto> commentList;
    //private UserInfoResponseDto userInfoResponseDto;
    //private List<Comment> commentList;

    public PostDetailResponseDto(Post post, List<CommentResponseDto> commentList, Long postLikesCnt){
        this.postKey = post.getId();
        this.postImageUrl = post.getPostImageUrl();
        this.color = post.getColor();
        this.limitCnt = post.getLimitCnt();
        this.complete = post.isComplete();  // boolean형은 get이 아니라 is로 가져온다.
        // 문단을 작성한 유저와 댓글을 작성한 유저를 클릭했을때 해당 유저(다른 사용자)
        // 페이지로 가기 위해서
        // 문단을 작성한 user 정보와 댓글을 작성한 user 정보를 response해줘야한다.
        // 즉,
        // paragraphList에서 userId 를 가지고 user 정보와 paragraphLike를 response 하고
        // commentList 에 있는 userId를 가지고 user정보를 와 commentLikes를 response 해라
        // 또한 postLikes 도 필요하다.
        this.postLikesCnt = postLikesCnt;
        this.commentList = commentList;
//        this.userInfoResponseDto.setUserKey(post.getUser().getId());
//        this.userInfoResponseDto.setUsername(post.getUser().getUsername());
//        this.userInfoResponseDto.setUserProfileImage(post.getUser().getUserProfileImage());
    }
}
