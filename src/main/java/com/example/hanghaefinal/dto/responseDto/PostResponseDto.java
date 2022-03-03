package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.Comment;
import com.example.hanghaefinal.model.Post;
import com.example.hanghaefinal.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostResponseDto {
    private Long postKey; // postId
    private String title;
    private String postUsername;
    private String postModifiedAt;
    private String postImageUrl;
    private String color;
    private int limitCnt;
    private boolean complete;
    // ----- 밑에 부터 post컬럼에 있는 값이 아닌 것 -----
    //private double postScore;
    //private List<CommentResponseDto> commnetList;
    private int postLikesCnt;
    private List<CommentResponseDto> commentList;

    // public PostResponseDto(Post post){
    public PostResponseDto(Post post, List<CommentResponseDto> commentList, int postLikesCnt){
        this.postKey = post.getId();
        this.title = post.getTitle();
        this.postUsername = post.getUser().getUsername();
        this.postModifiedAt = post.getModifiedAt().toString();
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
        this.postLikesCnt = postLikesCnt;
        this.commentList = commentList;
    }
}
