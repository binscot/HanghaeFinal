package com.example.hanghaefinal.repository;

import com.example.hanghaefinal.model.Alarm;
import com.example.hanghaefinal.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    Page<Alarm> findAllByUserIdOrderByIdDesc(Long userId, Pageable pageable);
    List<Alarm> findAllByUserId(Long userId);
    void deleteAllByPostId(Long postId);

    void deleteAllByUserId(Long userId);
}
