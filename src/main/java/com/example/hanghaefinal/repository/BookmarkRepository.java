package com.example.hanghaefinal.repository;

import com.example.hanghaefinal.model.Bookmark;
import com.example.hanghaefinal.model.Post;
import com.example.hanghaefinal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
        List<Bookmark> findAllByPostId(Long postId);

    List<Bookmark> findAllByUser(User user);

}
