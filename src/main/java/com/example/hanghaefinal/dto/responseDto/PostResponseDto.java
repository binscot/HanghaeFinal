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
    private String postImageUrl;
    private String color;
    private int limitCnt;
    private boolean complete;
    // ----- 밑에 부터 post컬럼에 있는 값이 아닌 것 -----
    //private double postScore;
    //private List<CommentResponseDto> commnetList;
    private User user;

    public PostResponseDto(Post post, Comment comment, User user){
        this.postKey = post.getId();
        this.postImageUrl = post.getPostImageUrl();
        this.color = post.getColor();
        this.limitCnt = post.getLimitCnt();
        this.complete = post.isComplete();  // boolean형은 get이 아니라 is로 가져온다.
        this.user = user;   // user정보를 모두 넣어준다.
        // paragraphList와 paragraphLike가 필요하다.
        // commentList
    }
}
