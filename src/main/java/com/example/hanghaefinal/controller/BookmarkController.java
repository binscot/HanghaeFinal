package com.example.hanghaefinal.Bookmark;

import com.example.hanghaefinal.dto.responseDto.BookmarkResponseDto;
import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.repository.UserRepository;
import com.example.hanghaefinal.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BookmarkController {

    private final com.example.hanghaefinal.Bookmark.BookmarkService bookmarkService;
    private final com.example.hanghaefinal.Bookmark.BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;

    @GetMapping("/bookmark")
    public List<BookmarkResponseDto> getBookmark(@AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        return bookmarkService.getBookmark(user);
    }


    @PostMapping("/bookmark/{postId}")
    public boolean addBookmarks(@PathVariable Long postId,
                           @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        return bookmarkService.addBookmark(postId, user);
    }

    @DeleteMapping("/bookmark/{bookmarkId}")
    public boolean deleteBookmark(@PathVariable Long postId,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        return bookmarkService.deleteBookmark(postId,user);
    }
}
