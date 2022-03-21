package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.Post;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookmarkResponseDto {
    private final Long bookmarkId;
    private List<BookmarkClickUserKeyResDto> bookmarkClickUserKeyResDtos;
    private Long bookmarkCnt;

    public BookmarkResponseDto(Long bookmarkId, List<BookmarkClickUserKeyResDto> bookmarkClickUserKeyResDtos, Long bookmarkCnt){
        this.bookmarkId = bookmarkId;
        this.bookmarkClickUserKeyResDtos = bookmarkClickUserKeyResDtos;
        this.bookmarkCnt = bookmarkCnt;
    }
}