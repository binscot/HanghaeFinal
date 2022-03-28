package com.example.hanghaefinal.repository;

import com.example.hanghaefinal.model.Bookmark;
import com.example.hanghaefinal.model.Post;
import com.example.hanghaefinal.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findAllByPostId(Long postId);

    List<Bookmark> findAllByUser(User user);

    void deleteAllByUser(User user);

    Optional<Bookmark> findByUserAndPost(User user, Post post);

    Long countByPost(Post post);

    Page<Bookmark> findAllByUserId(Long userId,Pageable pageable);
}
