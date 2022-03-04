package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

//@Builder
@Getter
@Setter
public class CommentResponseDto {
    private Long commentId;
    private String comment;
    private String commentModifiedAt;
    private Long commentUserId;
    private String commentUsername;
    private Long commentLikesCnt;
    private UserInfoResponseDto userInfoResponseDto;

    public CommentResponseDto(Comment comment, Long commentLikesCnt){
        this.commentId = comment.getId();
        this.comment = comment.getComment();
        this.commentModifiedAt = comment.getModifiedAt().toString();
        this.commentUserId = comment.getUser().getId();
        this.commentUsername = comment.getUser().getUsername();
        this.commentLikesCnt = commentLikesCnt;
        this.userInfoResponseDto = new UserInfoResponseDto(comment.getUser());
    }
}
