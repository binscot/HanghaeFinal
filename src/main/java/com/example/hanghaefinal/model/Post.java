package com.example.hanghaefinal.model;

import com.example.hanghaefinal.dto.requestDto.PostRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "post")
public class Post extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "post_image_url")
    private String postImageUrl;

    @Column(name = "color")
    private String color;

    @Column(name = "limit_cnt")
    private int limitCnt;

    @Column(name = "complete")  // nullable 고민하자
    private boolean complete;

    @Column(name = "writing")   // 누군가 문단 시작 버튼을 눌렀는지 여부
    private boolean writing;

    @Column(name = "writer") // 믄딘 시작 버튼을 누른 사람의 nickname
    private String writer; // 작성 완료 및 작성 취소시 삭제 또는 null

    @Column(name = "paragraph_start_time")  // 문단 작성 시작 버튼을 누른 시간
    private String paragraphStartTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Post(PostRequestDto requestDto, User user, String defaultImg){
        this.title = requestDto.getTitle();
        //this.postImageUrl = requestDto.getPostImageUrl();
        this.postImageUrl = defaultImg;
        this.color = requestDto.getColor();
        this.limitCnt = requestDto.getLimitCnt();
        this.user = user;
    }

    public void updatePostComplete(boolean bool){
        this.complete = bool;
    }

    public void updatePostByStart(boolean bool, String writer, LocalDateTime time){
        this.writing = bool;
        this.writer = writer;
        this.paragraphStartTime = String.valueOf(time);
    }

    public void updatePostWriting(boolean bool, String writer,String paragraphStartTime) {
        this.writing = bool;
        this.writer = writer;
        this.paragraphStartTime = paragraphStartTime;
    }

    public void updatePost(User anonymousUser) {
        this.user=anonymousUser;
    }
}