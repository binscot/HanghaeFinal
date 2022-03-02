package com.example.hanghaefinal.repository;

import com.example.hanghaefinal.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
    List<Comment> findAllByPostIdOrderByModifiedAtDesc(Long postId);
}
