package com.example.hanghaefinal.controller;


import com.example.hanghaefinal.dto.responseDto.BookmarkGetResponseDto;
import com.example.hanghaefinal.dto.responseDto.BookmarkResponseDto;
import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.security.UserDetailsImpl;
import com.example.hanghaefinal.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BookmarkController {

    private final BookmarkService bookmarkService;

    //북마크 조회
    @GetMapping("/bookmark")
    public List<BookmarkGetResponseDto> getBookmark(@RequestParam int page, @RequestParam int size, @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        return bookmarkService.getBookmark(page, size, user);
    }

    //북마크 등록
    @PostMapping("/bookmark/{postId}")
    public BookmarkResponseDto addBookmarks(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return bookmarkService.addBookmark(postId, userDetails);
    }
}
