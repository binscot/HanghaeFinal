package com.example.hanghaefinal.model;

import com.example.hanghaefinal.dto.responseDto.CommentResponseDto;
import com.example.hanghaefinal.dto.responseDto.UserInfoResponseDto;
import lombok.*;
import com.example.hanghaefinal.dto.requestDto.CommentRequestDto;
import com.example.hanghaefinal.model.Post;
import com.example.hanghaefinal.model.Timestamped;
import com.example.hanghaefinal.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment")
public class Comment extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "comment", nullable = false)
    private String comment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /*public CommentResponseDto toResponseDto() {
        return CommentResponseDto.builder()
                .commentId(this.id)
                .comment(this.comment)
                .commentModifiedAt(this.getModifiedAt().toString())
                .commentUserId(this.user.getId())
                .userInfoResponseDto(new UserInfoResponseDto(this.user))
                .build();
    }*/

    public Comment(CommentRequestDto commentRequestDto, Post post, User user) {
        this.comment = commentRequestDto.getComment();
        this.post = post;
        this.user = user;
    }

    public void update(CommentRequestDto requestDto) {
        this.comment = requestDto.getComment();
    }


}