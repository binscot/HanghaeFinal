package com.example.hanghaefinal.controller;

import com.example.hanghaefinal.dto.requestDto.NoticeRequestDto;
import com.example.hanghaefinal.dto.responseDto.NoticeResponseDto;
import com.example.hanghaefinal.security.UserDetailsImpl;
import com.example.hanghaefinal.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@Api(tags = {"Notice"})
public class NoticeController {

    private final NoticeService noticeService;

    @ApiOperation(value = "공지작성", notes = "공지작성")
    @PostMapping("/notice")
    public ResponseEntity<Boolean> createNotice(
            @ModelAttribute NoticeRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        noticeService.createNotice(requestDto,userDetails);
        return ResponseEntity.ok(true);
    }

    @ApiOperation(value = "공지 상세 조회", notes = "공지조회")
    @GetMapping("/notice/{noticeId}")
    public ResponseEntity<NoticeResponseDto> showNotice(@PathVariable Long noticeId){
        NoticeResponseDto noticeResponseDto = noticeService.showNotice(noticeId);
        return ResponseEntity.ok(noticeResponseDto);
    }

    @ApiOperation(value = "공지 전체 조회", notes = "공지조회")
    @GetMapping("/notice")
    public ResponseEntity<List<NoticeResponseDto>> showAllNotice(){
        List<NoticeResponseDto> noticeResponseDtoList = noticeService.showAllNotice();
        return ResponseEntity.ok(noticeResponseDtoList);
    }


    @ApiOperation(value = "공지수정", notes = "공지수정")
    @PutMapping("/notice/{noticeId}")
    public ResponseEntity<Boolean> updateNotice(
            @PathVariable Long noticeId,
            @ModelAttribute NoticeRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        noticeService.updateNotice(requestDto,userDetails,noticeId);
        return ResponseEntity.ok(true);
    }

    @ApiOperation(value = "공지삭제", notes = "공지삭제")
    @DeleteMapping("/notice/{noticeId}")
    public ResponseEntity<Boolean> deleteNotice(
            @PathVariable Long noticeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        noticeService.deleteNotice(userDetails,noticeId);
        return ResponseEntity.ok(true);
    }
}
