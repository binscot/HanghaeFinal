package com.example.hanghaefinal.service;

import com.example.hanghaefinal.dto.requestDto.ParagraphReqDto;
import com.example.hanghaefinal.model.Paragraph;
import com.example.hanghaefinal.model.Post;
import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.repository.ParagraphRepository;
import com.example.hanghaefinal.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ParagraphService {

    private final PostRepository postRepository;
    private final ParagraphRepository paragraphRepository;

    @Transactional
    public Boolean saveParagraph(ParagraphReqDto paragraphReqDto, Long postId, User user){
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("postId가 존재하지 않습니다.")
        );

        Paragraph paragraph = new Paragraph(paragraphReqDto.getParagraph(),user, post);
        paragraphRepository.save(paragraph);
        return true;
    }
}
