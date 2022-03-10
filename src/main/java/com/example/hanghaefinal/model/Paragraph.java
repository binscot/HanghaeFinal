package com.example.hanghaefinal.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "paragraph")
public class Paragraph extends Timestamped{
    public enum MessageType {
        ENTER, // 문단 작성 시작
        TALK, // 문단 작성 완료
        QUIT,
        DELETE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "paragraph")
    private String paragraph;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public Paragraph(String paragraph, User user, Post post){
        this.paragraph = paragraph;
        this.user = user;
        this.post = post;   // roomId를 postId로 대체하자
    }

}