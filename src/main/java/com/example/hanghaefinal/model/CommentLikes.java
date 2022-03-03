//package com.example.hanghaefinal.model;
//
//import com.example.hanghaefinal.dto.requestDto.CommentLikesRequestDto;
//import com.example.hanghaefinal.dto.requestDto.PostLikesRequestDto;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import javax.persistence.*;
//
//@NoArgsConstructor
//@Getter
//@Setter
//@Entity
//@Table(name = "comment_likes")
//public class CommentLikes {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id", nullable = false)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @ManyToOne
//    @JoinColumn(name = "comment_id", nullable = false)
//    private Comment comment;
//
//    public CommentLikes(CommentLikesRequestDto commentLikesRequestDto) {
//
//        this.id = commentLikesRequestDto.getComment().getId();
//    }
//}