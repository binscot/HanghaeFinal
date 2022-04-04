package com.example.hanghaefinal.model;

import com.example.hanghaefinal.dto.requestDto.ParagraphReqDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "paragraph")
public class Paragraph extends Timestamped{

    public enum MessageType {
        ENTER, // 구독시작
        START,  // 문단 작성 시작
        TALK, // 문단 작성 완료
        QUIT,
        STOP,
        DELETE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(columnDefinition = "TEXT", name = "paragraph")
    private String paragraph;

    @Column
    private MessageType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) // ...
    @JoinColumn(name = "post_id")
    private Post post;

    public Paragraph(String paragraph, User user, Post post){
        this.paragraph = paragraph;
        this.user = user;
        this.post = post;   // roomId를 postId로 대체하자
    }

    public Paragraph(ParagraphReqDto paragraphReqDto, User user, Post post){
        this.type = paragraphReqDto.getType();
        this.paragraph = paragraphReqDto.getParagraph();
        this.user = user;
        this.post = post;
    }

    public void updateUser(User anonymousUser) {
        this.user=anonymousUser;
    }

}