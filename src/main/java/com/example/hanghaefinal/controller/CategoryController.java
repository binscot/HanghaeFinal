package com.example.hanghaefinal.controller;

import com.example.hanghaefinal.dto.requestDto.CategoryRequestDto;
import com.example.hanghaefinal.dto.responseDto.CategoryResponseDto;
import com.example.hanghaefinal.dto.responseDto.PostResponseDto;
import com.example.hanghaefinal.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public List<CategoryResponseDto> showCategories() {
        return categoryService.showCategories();
    }

    @GetMapping("/category/posts")
    public List<PostResponseDto> showCategoryPosts(@RequestBody CategoryRequestDto categoryRequestDto){
        return categoryService.showCategoryPosts(categoryRequestDto);
    }
}
