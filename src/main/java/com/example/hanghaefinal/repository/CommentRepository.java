package com.example.hanghaefinal.repository;

import com.example.hanghaefinal.model.Comment;
import com.example.hanghaefinal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);
    //List<Comment> findByPostId(Long postId);
    List<Comment> findAllByPostIdOrderByModifiedAtDesc(Long postId);

    void deleteAllByUser(User user);
}

