package com.example.hanghaefinal.model;


import com.example.hanghaefinal.dto.requestDto.BookmarkRequestDto;
import com.example.hanghaefinal.dto.requestDto.PostLikesRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "book_mark")
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //@OneToOne(fetch = FetchType.LAZY)
    //@JsonIgnore
    @OneToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;



    public Bookmark(BookmarkRequestDto bookmarkRequestDto) {

        this.user = bookmarkRequestDto.getUser();
        this.post = bookmarkRequestDto.getPost();
    }
}