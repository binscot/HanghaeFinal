package com.example.hanghaefinal.service;

import com.example.hanghaefinal.dto.responseDto.BookmarkResponseDto;
import com.example.hanghaefinal.model.Badge;
import com.example.hanghaefinal.model.Bookmark;
import com.example.hanghaefinal.model.Post;
import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.repository.BadgeRepository;
import com.example.hanghaefinal.repository.BookmarkRepository;
import com.example.hanghaefinal.repository.PostRepository;
import com.example.hanghaefinal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.ArrayList;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;
    private final BadgeRepository badgeRepository;


    //북마크조회
    public List<BookmarkResponseDto> getBookmark(User user) {

        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        List<BookmarkResponseDto> bookmarkResponseDtos =new ArrayList<>();

        for(Bookmark bookmark : bookmarkList){
            if(bookmark.getUser().getId().equals(user.getId())){
                BookmarkResponseDto bookmarkResponseDto = new BookmarkResponseDto(
                        bookmark.getId(),
                        bookmark.getPost().getId(),
                        bookmark.getUser().getId()
                );

                bookmarkResponseDtos.add(bookmarkResponseDto);
            }
        }
        return bookmarkResponseDtos;
    }


    //북마크생성
    public boolean addBookmark(@PathVariable Long postId,
                                     User user) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        Bookmark bookmark = new Bookmark(user, post);
        bookmarkRepository.save(bookmark);

        //북마크 뱃지 로직 구현
        User postUser = post.getUser();
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        List<Bookmark> bookmarkPostUserList = new ArrayList<>();
        for (Bookmark bookmark1:bookmarkList){
            if (bookmark1.getUser()==postUser){
                bookmarkPostUserList.add(bookmark1);
            }
        }
        if (bookmarkPostUserList.size()==9){
            Badge badge = new Badge();
            badge.setBadgeName("인기쟁이");
            badge.setUser(postUser);
            badgeRepository.save(badge);
        }
        return true;
    }


    //북마크 삭제
    public boolean deleteBookmark(@PathVariable Long postId, User user) {

        List<Bookmark> bookmarksList= bookmarkRepository.findAllByPostId(postId);
        Long userId = user.getId();
        for(Bookmark bookmark:bookmarksList){
            if(bookmark.getUser().getId().equals(userId)){
                bookmarkRepository.deleteById(bookmark.getId());
            }
        }
        return false;
    }


}