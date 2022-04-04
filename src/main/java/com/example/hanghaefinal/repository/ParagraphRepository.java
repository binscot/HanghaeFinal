package com.example.hanghaefinal.repository;

import com.example.hanghaefinal.model.Paragraph;
import com.example.hanghaefinal.model.ParagraphLikes;
import com.example.hanghaefinal.model.Post;
import com.example.hanghaefinal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ParagraphRepository extends JpaRepository<Paragraph, Long> {
    List<Paragraph> findAllByPostIdOrderByModifiedAtDesc(Long postId);

    List<Paragraph> findAllByUser(User user);

    //@Query(value = "select distinct p from Paragraph p join fetch p.user join fetch p.post")
    List<Paragraph> findAllByPostId(Long postId);

    List<Paragraph> findAllByUserId(Long userId);

    Long countByPost(Post post);


//    int countByPost(Post post);
}
