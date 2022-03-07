package com.example.hanghaefinal.repository;

import com.example.hanghaefinal.model.AttendanceCheck;
import com.example.hanghaefinal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceCheckRepository extends JpaRepository<AttendanceCheck, Long> {
    Optional<AttendanceCheck> findByDate(int date);

    List<AttendanceCheck> findAllByUser(User user);
}
