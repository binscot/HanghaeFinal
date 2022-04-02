package com.example.hanghaefinal.service;

import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class LevelService {

    private final UserRepository userRepository;

    @Transactional
    public void LevelCheck(User user) {

        int userPoint = user.getPoint();

        if( 5 <=  userPoint && userPoint< 15 && !user.getLevel().equals("lv.2 견습 작가"))
        {
            user.updateLevel("lv.2 견습 작가");
            userRepository.save(user);

        } else if(15 <= userPoint && userPoint< 30 && !user.getLevel().equals("lv.3 작가"))

        {
            user.updateLevel("lv.3 작가");
            userRepository.save(user);

        } else if(30 <= userPoint && userPoint< 50 && !user.getLevel().equals("lv.4 프로 작가"))

        {
            user.updateLevel("lv.4 프로 작가");
            userRepository.save(user);

        } else if(50 <= userPoint && userPoint< 80 && !user.getLevel().equals("lv.5 베스트셀러 작가"))

        {
            user.updateLevel("lv.5 베스트셀러 작가");
            userRepository.save(user);

        } else if(80 <= userPoint && !user.getLevel().equals("lv.6 셰익스피어")) {
            user.updateLevel("lv.6 셰익스피어");
            userRepository.save(user);
        }
    }
}
