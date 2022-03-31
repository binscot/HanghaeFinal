package com.example.hanghaefinal.model;

import com.example.hanghaefinal.Enum.AlarmType;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Alarm extends Timestamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private Long userId; // 알람 받는 대상

    @Column
    @Enumerated(value = EnumType.STRING)
    private AlarmType type;

    @Column
    private Long pubId; // 채팅방 생성자

    @Column
    private Long postId; // 게시물 번호

    @Column
    private String alarmMessage;

    @Column
    private Boolean isRead;
}


