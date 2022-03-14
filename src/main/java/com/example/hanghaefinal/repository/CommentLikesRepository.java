package com.example.hanghaefinal.repository;


import com.example.hanghaefinal.model.Comment;
import com.example.hanghaefinal.model.CommentLikes;
import com.example.hanghaefinal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentLikesRepository extends JpaRepository<CommentLikes, Long> {

    Optional<CommentLikes> findByUserAndComment(User user, Comment comment);
    Long countByComment(Comment comment);

    void deleteAllByUser(User user);

    List<CommentLikes> findAllByPostId(Long commentId);
}

