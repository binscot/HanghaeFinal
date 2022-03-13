package com.example.hanghaefinal.repository;

import com.example.hanghaefinal.model.Paragraph;
import com.example.hanghaefinal.model.ParagraphLikes;
import com.example.hanghaefinal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParagraphLikesRepository extends JpaRepository<ParagraphLikes, Long> {
    Optional<ParagraphLikes> findByUserAndParagraph(User user, Paragraph paragraph);
    Long countByParagraph(Paragraph paragraph);
    //ParagraphLikes findByUserId();
    List<ParagraphLikes> findAllByParagraphId(Long paragraphKey);
}
