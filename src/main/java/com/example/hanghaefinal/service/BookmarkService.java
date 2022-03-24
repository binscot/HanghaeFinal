package com.example.hanghaefinal.service;

import com.example.hanghaefinal.dto.requestDto.BookmarkRequestDto;
import com.example.hanghaefinal.dto.responseDto.*;
import com.example.hanghaefinal.model.*;
import com.example.hanghaefinal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;


import javax.transaction.Transactional;
import java.util.ArrayList;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final BadgeRepository badgeRepository;
    private final CategoryRepository categoryRepository;

    //북마크조회
    public List<BookmarkGetResponseDto> getBookmark(int page, int size, User user) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Bookmark> bookmarkList = bookmarkRepository.findAll(pageable);
        List<BookmarkGetResponseDto> bookmarkGetResponseDtos =new ArrayList<>();

        for(Bookmark bookmark : bookmarkList){
            if(bookmark.getUser().getId().equals(user.getId())){

                List<Category> categoryList = categoryRepository.findAllByPostIdOrderByModifiedAtDesc(bookmark.getPost().getId());
                List<CategoryResponseDto> categoryResDtoList = new ArrayList<>();

                // List<Category>에 있는 정보를 List<CategoryResponseDto> 에 담는다.
                for (Category category : categoryList) {
                    categoryResDtoList.add(new CategoryResponseDto(category));
                }

                BookmarkGetResponseDto bookmarkGetResponseDto = new BookmarkGetResponseDto(
                        bookmark.getId(),
                        bookmark.getPost(),
                        bookmark.getUser().getId(),
                        categoryResDtoList
                );

                bookmarkGetResponseDtos.add(bookmarkGetResponseDto);
            }
        }
        return bookmarkGetResponseDtos;
    }


    //북마크생성
    @Transactional
    public BookmarkResponseDto addBookmark(@PathVariable Long postId, Long userId) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("유저정보가 없습니다.")
        );

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        Bookmark bookmarkCheck = bookmarkRepository.findByUserAndPost(user, post).orElse(null);

        if (bookmarkCheck == null) {
            BookmarkRequestDto bookmarkRequestDto = new BookmarkRequestDto(user, post);
            Bookmark bookmark = new Bookmark(bookmarkRequestDto);
            bookmarkRepository.save(bookmark);
        } else {
            bookmarkRepository.deleteById(bookmarkCheck.getId());
        }

        List<Bookmark> bookmark = bookmarkRepository.findAllByPostId(postId);
        List<BookmarkClickUserKeyResDto> bookmarkClickerUserKeyResponseDtos = new ArrayList<>();
        for (Bookmark bookmarkTemp : bookmark) {
            bookmarkClickerUserKeyResponseDtos.add(new BookmarkClickUserKeyResDto(bookmarkTemp));
        }

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

        return  new BookmarkResponseDto(postId, bookmarkClickerUserKeyResponseDtos, bookmarkRepository.countByPost(post));
    }


//    //북마크 삭제
//    public boolean deleteBookmark(@PathVariable Long postId, User user) {
//
//        List<Bookmark> bookmarksList= bookmarkRepository.findAllByPostId(postId);
//        Long userId = user.getId();
//        for(Bookmark bookmark:bookmarksList){
//            if(bookmark.getUser().getId().equals(userId)){
//                bookmarkRepository.deleteById(bookmark.getId());
//            }
//        }
//        return false;
//    }


}