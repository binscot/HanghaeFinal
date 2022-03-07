package com.example.hanghaefinal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "attendanceCheck")
public class AttendanceCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "date", nullable = false)
    private int date;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
