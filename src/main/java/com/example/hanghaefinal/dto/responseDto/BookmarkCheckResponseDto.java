package com.example.hanghaefinal.dto.responseDto;

import com.example.hanghaefinal.model.Post;
import lombok.Getter;
import lombok.Setter;

    @Getter
    @Setter
    public class BookmarkCheckResponseDto {
        private final Long bookmarkId;


        public BookmarkCheckResponseDto(Long bookmarkId){

            this.bookmarkId = bookmarkId;
        }
    }

