package com.example.hanghaefinal.repository;

import com.example.hanghaefinal.model.AttendanceCheck;
import com.example.hanghaefinal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceCheckRepository extends JpaRepository<AttendanceCheck, Long> {
    List<AttendanceCheck> findAllByUser(User user);

    void deleteAllByUser(User user);
}
