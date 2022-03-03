package com.example.hanghaefinal.model;

import com.example.hanghaefinal.dto.responseDto.CommentResponseDto;
import com.example.hanghaefinal.dto.responseDto.UserInfoResponseDto;
import lombok.*;

import javax.persistence.*;

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

    public CommentResponseDto toResponseDto() {
        return CommentResponseDto.builder()
                .commentId(this.id)
                .comment(this.comment)
                .commentModifiedAt(this.getModifiedAt().toString())
                .commentUserId(this.user.getId())
                .userInfoResponseDto(new UserInfoResponseDto(this.user))
                .build();
    }
}