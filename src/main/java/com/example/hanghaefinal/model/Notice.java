package com.example.hanghaefinal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor // 기본 생성자를 만들어줍니다.
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "notice")
public class Notice extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "noticeImg")
    private String noticeImg;

    public Notice(String title, String content, String noticeImg) {
        this.title=title;
        this.content=content;
        this.noticeImg=noticeImg;

    }

    public void updateNotice(String title, String content, String noticeImg){
        this.title=title;
        this.content=content;
        this.noticeImg=noticeImg;
    }
}