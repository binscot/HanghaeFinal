package com.example.hanghaefinal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "post")
public class Post extends Timestamped {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "post_image_url")
    private String postImageUrl;

    @Column(name = "color")
    private String color;

    @Column(name = "limit_cnt")
    private String limitCnt;

    @Column(name = "complete")
    private boolean complete;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}