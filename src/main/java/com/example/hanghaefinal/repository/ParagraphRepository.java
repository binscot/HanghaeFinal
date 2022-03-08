package com.example.hanghaefinal.repository;

import com.example.hanghaefinal.model.Paragraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParagraphRepository extends JpaRepository<Paragraph, Long> {
    List<Paragraph> findAllByPostIdOrderByModifiedAtDesc(Long postId);
}
