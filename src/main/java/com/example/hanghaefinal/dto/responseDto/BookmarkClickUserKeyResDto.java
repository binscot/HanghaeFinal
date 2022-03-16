package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.Bookmark;
import lombok.Getter;

@Getter
public class BookmarkClickUserKeyResDto {
    private Long userKey;

    public BookmarkClickUserKeyResDto(Bookmark bookmark){
        this.userKey = bookmark.getUser().getId();
    }
}
