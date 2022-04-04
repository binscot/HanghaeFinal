package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.Category;
import com.example.hanghaefinal.model.Post;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
public class PostDetailResponseDto {
    private Long postKey; // postId
    private String title;
    private String postUsername;
    private String postModifiedAt;
    private String postImageUrl;
    private String color;
    private int limitCnt;
    private boolean complete;
    private boolean writing;
    private String writer;
    private String paragraphStartTime;
    // ----- 밑에 부터 post컬럼에 있는 값이 아닌 것 -----
    //private double postScore;
    //private List<CommentResponseDto> commnetList;
    private Long postLikesCnt;
    private List<PostLikeClickersResponseDto> postLikeClickersResponseDtoList;
    private int bookmarkLikesCnt;
    private List<BookmarkClickUserKeyResDto> bookmarkClickUserKeyResDtoList;
    private List<ParagraphResDto> paragraphResDtoList;
    private List<CategoryResponseDto> categoryList;
    private List<CommentResponseDto> commentList;

    public PostDetailResponseDto(Post post,
                                 List<PostLikeClickersResponseDto> postLikeClickersResponseDtoList,
                                 List<BookmarkClickUserKeyResDto> bookmarkClickUserKeyResDtoList,
                                 List<ParagraphResDto> paragraphResDtoList,
                                 List<CommentResponseDto> commentList,
                                 List<CategoryResponseDto> categoryList,
                                 Long postLikesCnt, String postUsername){
        this.postKey = post.getId();
        this.title = post.getTitle();
        this.postUsername = postUsername;
        this.postModifiedAt = post.getModifiedAt().toString();
        this.postImageUrl = post.getPostImageUrl();
        this.color = post.getColor();
        this.limitCnt = post.getLimitCnt();
        this.complete = post.isComplete();  // boolean형은 get이 아니라 is로 가져온다.
        this.writing = post.isWriting();
        this.writer = post.getWriter();
        this.paragraphStartTime = post.getParagraphStartTime();
        this.postLikesCnt = postLikesCnt;
        this.postLikeClickersResponseDtoList = postLikeClickersResponseDtoList;
        this.bookmarkLikesCnt = bookmarkClickUserKeyResDtoList.size();
        this.bookmarkClickUserKeyResDtoList = bookmarkClickUserKeyResDtoList;
        this.paragraphResDtoList = paragraphResDtoList;

        this.categoryList = categoryList;
        this.commentList = commentList;
    }


    public PostDetailResponseDto(Post post,
                                 List<PostLikeClickersResponseDto> postLikeClickersResponseDtoList,
                                 List<BookmarkClickUserKeyResDto> bookmarkClickUserKeyResDtoList,
                                 List<ParagraphResDto> paragraphResDtoList,
                                 List<CategoryResponseDto> categoryList,
                                 Long postLikesCnt, String postUsername
    ){
        this.postKey = post.getId();
        this.title = post.getTitle();
        this.postUsername = postUsername;
        this.postModifiedAt = post.getModifiedAt().toString();
        this.postImageUrl = post.getPostImageUrl();
        this.color = post.getColor();
        this.limitCnt = post.getLimitCnt();
        this.complete = post.isComplete();  // boolean형은 get이 아니라 is로 가져온다.
        this.writing = post.isWriting();
        this.writer = post.getWriter();
        this.paragraphStartTime = post.getParagraphStartTime();
        this.postLikesCnt = postLikesCnt;
        this.postLikeClickersResponseDtoList = postLikeClickersResponseDtoList;
        this.bookmarkLikesCnt = bookmarkClickUserKeyResDtoList.size();
        this.bookmarkClickUserKeyResDtoList = bookmarkClickUserKeyResDtoList;
        this.paragraphResDtoList = paragraphResDtoList;
        this.categoryList = categoryList;
    }


}
