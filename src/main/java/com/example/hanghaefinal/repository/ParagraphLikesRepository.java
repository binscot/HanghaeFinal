package com.example.hanghaefinal.repository;

import com.example.hanghaefinal.model.Paragraph;
import com.example.hanghaefinal.model.ParagraphLikes;
import com.example.hanghaefinal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ParagraphLikesRepository extends JpaRepository<ParagraphLikes, Long> {
    Optional<ParagraphLikes> findByUserAndParagraph(User user, Paragraph paragraph);
    Long countByParagraph(Paragraph paragraph);

    //@Query(value = "select p from ParagraphLikes p join fetch p.user join fetch p.paragraph")
    List<ParagraphLikes> findAllByParagraphId(Long paragraphKey);

    void deleteAllByUser(User user);
}
