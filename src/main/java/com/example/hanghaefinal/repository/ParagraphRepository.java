package com.example.hanghaefinal.repository;

import com.example.hanghaefinal.model.Paragraph;
import com.example.hanghaefinal.model.ParagraphLikes;
import com.example.hanghaefinal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParagraphRepository extends JpaRepository<Paragraph, Long> {
    List<Paragraph> findAllByPostIdOrderByModifiedAtDesc(Long postId);

    List<Paragraph> findAllByUser(User user);

    List<Paragraph> findAllByPostId(Long postId);

    List<Paragraph> findAllByUserId(Long userId);
}
