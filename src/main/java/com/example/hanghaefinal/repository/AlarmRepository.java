package com.example.hanghaefinal.repository;

import com.example.hanghaefinal.model.Alarm;
import com.example.hanghaefinal.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    Page<Alarm> findAllByUserIdOrderByIdDesc(Long userId, Pageable pageable);

    void deleteAllByUser(User user);
}
