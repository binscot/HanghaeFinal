package com.example.hanghaefinal.model;

import com.example.hanghaefinal.dto.requestDto.ParagraphLikesReqDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "paragraph_likes")
public class ParagraphLikes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "paragraph_id", nullable = false)
    private Paragraph paragraph;

    public ParagraphLikes(ParagraphLikesReqDto paragraphLikesReqDto){
        this.user = paragraphLikesReqDto.getUser();
        this.paragraph = paragraphLikesReqDto.getParagraph();
    }

}


