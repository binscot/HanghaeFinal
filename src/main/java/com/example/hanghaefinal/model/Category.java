package com.example.hanghaefinal.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "category")
public class Category extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "category")
    private String category;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public Category(String category, Post post){
        this.category = category;
        this.post = post;
    }
}