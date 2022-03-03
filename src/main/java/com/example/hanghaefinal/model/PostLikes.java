package com.example.hanghaefinal.model;

import com.example.hanghaefinal.dto.requestDto.PostLikesRequestDto;
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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public PostLikes(PostLikesRequestDto postLikesRequestDto) {

        this.user = postLikesRequestDto.getUser();
        this.post = postLikesRequestDto.getPost();
    }
}