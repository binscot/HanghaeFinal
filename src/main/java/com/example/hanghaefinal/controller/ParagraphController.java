package com.example.hanghaefinal.controller;

import com.example.hanghaefinal.dto.requestDto.ParagraphReqDto;
import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.security.UserDetailsImpl;
import com.example.hanghaefinal.service.ParagraphService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ParagraphController {

    private final ParagraphService paragraphService;

    @PostMapping("/paragraph/{postId}")
    public Boolean saveParagraph(@PathVariable Long postId,
                                 @RequestBody ParagraphReqDto paragraphReqDto,
                                 @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        if(userDetails != null) {
            User user = userDetails.getUser();
            paragraphService.saveParagraph(paragraphReqDto, postId, user);
        } else throw new IllegalArgumentException("로그인한 유저 정보가 없습니다.");
        return true;
    }
}
