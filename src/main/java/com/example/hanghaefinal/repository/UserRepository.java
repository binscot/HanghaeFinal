package com.example.hanghaefinal.repository;

import com.example.hanghaefinal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByNickName(String nickName);

    Optional<User> findByKakaoId(String kakaoId);

}
