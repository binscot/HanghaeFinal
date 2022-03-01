package com.example.hanghaefinal.repository;

import com.example.hanghaefinal.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
