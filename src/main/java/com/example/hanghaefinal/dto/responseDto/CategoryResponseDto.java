package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.Category;
import lombok.Getter;

@Getter
public class CategoryResponseDto {
    private String category;

    public CategoryResponseDto(Category category){
        this.category = category.getCategoryName();
    }
}
