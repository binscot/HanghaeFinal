package com.example.hanghaefinal.service;

import com.example.hanghaefinal.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class LevelService {

    @Transactional
    public void LevelCheck(User user) {

        int userPoint = user.getPoint();

        if(userPoint< 5)
        {
            user.setLevel("lv.1 작가 지망생");
        } else if(6<=userPoint && userPoint< 15)

        {
            user.setLevel("lv.2 견습 작가");
        } else if(16<=userPoint && userPoint< 30)

        {
            user.setLevel("lv.3 작가");
        } else if(31<=userPoint && userPoint< 50)

        {
            user.setLevel("lv.4 프로 작가");
        } else if(50<=userPoint && userPoint< 80)

        {
            user.setLevel("lv.5 베스트셀러 작가 ");
        }
        user.setLevel("lv.6 셰익스피어");

    }
}
