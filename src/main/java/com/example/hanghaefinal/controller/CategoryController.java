package com.example.hanghaefinal.controller;

import com.example.hanghaefinal.dto.requestDto.CategoryRequestDto;
import com.example.hanghaefinal.dto.responseDto.CategoryResponseDto;
import com.example.hanghaefinal.dto.responseDto.PostResponseDto;
import com.example.hanghaefinal.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public List<CategoryResponseDto> showCategories() {
        return categoryService.showCategories();
    }

    // 요청하는 카테고리에 해당하는 post목록을 반환한다. ex) '드라마'로 요청하면 '드라마'카테고리를 포함하는 post목록을 반환
    @PostMapping("/category/posts")
    public List<PostResponseDto> showCategoryPosts(@RequestBody CategoryRequestDto categoryRequestDto,
                                                   @RequestParam int page,
                                                   @RequestParam int size){
        return categoryService.showCategoryPosts(categoryRequestDto, page, size);
    }
}
