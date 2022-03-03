package com.example.hanghaefinal.repository;

import com.example.hanghaefinal.model.PostLikes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostLikesRepository extends JpaRepository<PostLikes, Long> {
    List<PostLikes> findAllByPostId(Long id);
}
