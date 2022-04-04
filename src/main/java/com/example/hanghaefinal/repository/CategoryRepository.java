package com.example.hanghaefinal.repository;

import com.example.hanghaefinal.model.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByPostIdOrderByModifiedAtDesc(Long postId);



    List<Category> findByCategoryNameOrderByModifiedAtDesc(String categoryName, Pageable pageable);


    List<Category> findAllByPostId(Long postId);
}
