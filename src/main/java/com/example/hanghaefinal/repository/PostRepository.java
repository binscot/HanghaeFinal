package com.example.hanghaefinal.repository;

import com.example.hanghaefinal.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByModifiedAtDesc();
    // findAll By OrderBy ModifiedAt Desc
    // 모두 찾아라 순서에따라 ModifiedAt을 기준으로 내림차순으로
    // 규칙에 맞게만 써주면 jpa가 알아서 sql문을 짜준다
    // modifiedAt 은 Timestamped 에 있다.
    List<Post> findByTitleContaining(String keyword);
    List<Post> findAllByUserIdOrderByModifiedAtDesc(Long userId);
}
