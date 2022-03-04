package com.example.hanghaefinal.repository;

import com.example.hanghaefinal.model.PostLikes;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.hanghaefinal.model.Post;
import com.example.hanghaefinal.model.User;

import java.util.List;

import java.util.Optional;

public interface PostLikesRepository extends JpaRepository<PostLikes, Long> {
    List<PostLikes> findAllByPostId(Long id);

    Optional<PostLikes> findByUserAndPost(User user, Post post);
    Long countByPost(Post post);

}
