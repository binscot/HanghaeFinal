package com.example.hanghaefinal.model;

import com.example.hanghaefinal.dto.requestDto.PostRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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

    @Column(name = "complete")
    private boolean complete;

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

    public void updatePost(boolean bool){
        this.complete = bool;
    }
}