package com.example.hanghaefinal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "badge")
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "badge_name", nullable = false)
    private String badgeName;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}