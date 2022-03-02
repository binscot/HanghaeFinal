package com.example.hanghaefinal.service;

import com.example.hanghaefinal.dto.requestDto.PostRequestDto;
import com.example.hanghaefinal.model.Post;
import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.repository.PostRepository;
import com.example.hanghaefinal.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final S3Uploader s3Uploader;

    public Boolean uploadImageFile(MultipartFile multipartFile, PostRequestDto requestDto) throws IOException {
        //String originalFileName = multipartFile.getOriginalFilename();
        //String convertedFileName = UUID.randomUUID() + originalFileName;
        //requestDto.setImageUrl(convertedFileName);
        String dirName = "image";
        //s3Uploader.upload(multipartFile, convertedFileName);

        String uploadUrl =  s3Uploader.upload(multipartFile, dirName);
        log.info("~~~ uploadUrl : " + uploadUrl );
        requestDto.setImageUrl(uploadUrl);
        return true;
    }

    public Boolean savePost(PostRequestDto postRequestDto, User user){
        Post post = new Post(postRequestDto, user);
        postRepository.save(post);

        return true;
    }

}
