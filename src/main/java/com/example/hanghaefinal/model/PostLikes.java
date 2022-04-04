package com.example.hanghaefinal.model;

import com.example.hanghaefinal.dto.requestDto.PostLikesRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "post_likes")
public class PostLikes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // User 를 불러오면 user 안에 연관관계 eager가 있으면 그 연관관관계를 또 불러온다.(n+1쿼리문제)
    // LAZY면 findAll만 하면 n+1 문제 발생 안하지만, LAZY를 사용하면 User는 postLikes.getUser() 할 때 n+1 문제 발생한다.
    // -> paging 안하면 fetch join, paging 하면 배치사이즈
    @ManyToOne(fetch = FetchType.LAZY) // ManyToOne의 default는 EAGER,
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public PostLikes(PostLikesRequestDto postLikesRequestDto) {

        this.user = postLikesRequestDto.getUser();
        this.post = postLikesRequestDto.getPost();
    }
}