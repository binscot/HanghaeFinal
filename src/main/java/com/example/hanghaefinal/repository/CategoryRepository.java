package com.example.hanghaefinal.repository;

import com.example.hanghaefinal.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
