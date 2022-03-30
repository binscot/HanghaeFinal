package com.example.hanghaefinal.service;

import com.example.hanghaefinal.dto.requestDto.NoticeRequestDto;
import com.example.hanghaefinal.dto.responseDto.NoticeResponseDto;
import com.example.hanghaefinal.exception.exception.AdminOnlyException;
import com.example.hanghaefinal.exception.exception.NoticeNotFoundException;
import com.example.hanghaefinal.model.Notice;
import com.example.hanghaefinal.repository.NoticeRepository;
import com.example.hanghaefinal.security.UserDetailsImpl;
import com.example.hanghaefinal.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {
    private final S3Uploader s3Uploader;
    private final NoticeRepository noticeRepository;

    @Transactional
    public void createNotice(
            NoticeRequestDto requestDto,
            UserDetailsImpl userDetails
    ) throws IOException {

        if (!checkAdmin(userDetails)){
           throw new AdminOnlyException("관리자만 접근 가능합니다.");
        }
        MultipartFile multipartFile = requestDto.getNoticeImg();
        //로고 이미지로 수정 해야함
        String noticeImg = "https://binscot-bucket.s3.ap-northeast-2.amazonaws.com/static/photo.png";
        if (!Objects.equals(multipartFile.getOriginalFilename(), "foo.txt"))
            noticeImg = s3Uploader.upload(multipartFile, "static");

        Notice notice = new Notice(requestDto.getTitle(),
                requestDto.getContent(),
                noticeImg
        );
        noticeRepository.save(notice);

    }

    @Transactional
    public void updateNotice(
            NoticeRequestDto requestDto,
            UserDetailsImpl userDetails,
            Long noticeId
    ) throws IOException{

        if (!checkAdmin(userDetails)){
            throw new AdminOnlyException("관리자만 접근 가능합니다.");
        }
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(
                () -> new NoticeNotFoundException("공지가 존재하지 않습니다.")
        );
        MultipartFile multipartFile = requestDto.getNoticeImg();
        //로고 이미지로 수정 해야함
        String noticeImg = "https://binscot-bucket.s3.ap-northeast-2.amazonaws.com/static/photo.png";
        if (!Objects.equals(multipartFile.getOriginalFilename(), "foo.txt"))
            noticeImg = s3Uploader.upload(multipartFile, "static");

        notice.updateNotice(requestDto.getTitle(),
                requestDto.getContent(),
                noticeImg
        );
        noticeRepository.save(notice);
    }

    @Transactional
    public void deleteNotice(UserDetailsImpl userDetails, Long noticeId) {
        if (!checkAdmin(userDetails)){
            throw new AdminOnlyException("관리자만 접근 가능합니다.");
        }
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(
                () -> new NoticeNotFoundException("공지가 존재하지 않습니다.")
        );
        noticeRepository.delete(notice);
    }

    public Boolean checkAdmin(UserDetailsImpl userDetails){
        return Objects.equals(userDetails.getUser().getUsername(), "admin");
    }

    public NoticeResponseDto showNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(
                () -> new NoticeNotFoundException("공지가 존재하지 않습니다.")
        );
        return new NoticeResponseDto(
                notice.getTitle(),
                notice.getContent(),
                notice.getNoticeImg(),
                notice.getCreatedAt(),
                notice.getModifiedAt()
        );
    }


    public List<NoticeResponseDto> showAllNotice() {
        List<NoticeResponseDto> noticeResponseDtoList = new ArrayList<>();
        List<Notice> noticeList = noticeRepository.findAll();
        for (Notice notice:noticeList){
            NoticeResponseDto noticeResponseDto = new NoticeResponseDto(
                    notice.getTitle(),
                    notice.getContent(),
                    notice.getNoticeImg(),
                    notice.getCreatedAt(),
                    notice.getModifiedAt()
            );
            noticeResponseDtoList.add(noticeResponseDto);
        }
        return noticeResponseDtoList;
    }
}
