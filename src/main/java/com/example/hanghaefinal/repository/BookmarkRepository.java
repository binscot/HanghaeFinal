package com.example.hanghaefinal.Bookmark;

import com.example.hanghaefinal.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
        List<Long> findAllByUserId(Long userId);
        List<Bookmark> findByPostId(Long postId);

        void deleteByPostId(Long postId);

        List<Bookmark> findAllByPostId(Long postid);

    List<Bookmark> findAllByOrderByCreatedAtDesc();
    //List<Bookmarks> findAllByOrderByCreatedAtDesc();

    }
