package com.example.hanghaefinal.service;

import com.example.hanghaefinal.dto.requestDto.EmailRequestDto;
import com.example.hanghaefinal.dto.responseDto.MailKeyResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public MailKeyResponseDto mailCheck(EmailRequestDto requestDto){
        Random random = new Random(); //난수 생성
        String key=""; // 인증번호
        String email = requestDto.getEmail();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        //message.setFrom("ohohgeunhy@gamil.com");

        for(int i = 0;i<3;i++){
            int index = random.nextInt(25)+65;

            key+=(char)index;
        }
        int numIndex = random.nextInt(9999)+1000;
        key +=numIndex;
        message.setSubject("인증번호 입력을 위한 메일 전송");
        message.setText( " 안녕하세요! \n위라이트 입니다!\n회원가입을 위한 인증번호를 보내드립니다! \n회원가입 창으로 돌아가 인증번호를 입력해 주세요! \n 인증 번호 : " + key);

        javaMailSender.send(message);
        MailKeyResponseDto mailKeyResponseDto = new MailKeyResponseDto();
        mailKeyResponseDto.setKey(key);
        mailKeyResponseDto.setOk(true);
        return mailKeyResponseDto;
    }
}
