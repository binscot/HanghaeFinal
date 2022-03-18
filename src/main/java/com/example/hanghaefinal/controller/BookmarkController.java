package com.example.hanghaefinal.controller;

import com.example.hanghaefinal.dto.responseDto.BookmarkCheckResponseDto;
import com.example.hanghaefinal.dto.responseDto.BookmarkInfoResponseDto;
import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.repository.UserRepository;
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
    public List<BookmarkInfoResponseDto> getBookmark(@AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        return bookmarkService.getBookmark(user);
    }

    @PostMapping("/bookmark/{postId}")
    public BookmarkCheckResponseDto addBookmarks(@PathVariable Long postId,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails){
        return bookmarkService.addBookmark(postId, userDetails.getUser().getId());
    }

    @DeleteMapping("/bookmark/{postId}")
    public boolean deleteBookmark(@PathVariable Long postId,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        return bookmarkService.deleteBookmark(postId,user);
    }
}
