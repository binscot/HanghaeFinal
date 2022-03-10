package com.example.hanghaefinal.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class RedisRepository {

    public static final String ENTER_INFO = "ENTER_INFO";
    public static final String RANDOM_NUM = "RANDOM_NUM";
    public static final String USER_INOUT = "USER_INOUT";

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> stringHashOpsEnterInfo;

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> stringHashOpsItemInfo;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, Integer> longOperations;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, Boolean> userInOutOperations;


    // shorts 조회시 Ip와 randomNum 저장
    public void setRandomNumIp(String sessionId, int randomNum) {
        longOperations.set(RANDOM_NUM + "_" + sessionId, randomNum);
    }

    // shorts 조회시 Ip로 randomNum 조회
    public int getRandomNumIp(String sessionId) {
        return Optional.ofNullable(longOperations.get(RANDOM_NUM + "_" + sessionId)).orElse(-1);
    }

    // sessionId로 inOutKey 등록
    public void setSessionUserInfo(String sessionId, String roomId, String name) {
        stringHashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId + "_" + name);
    }

    // sessionId로 inOutKey 찾아오기
    public String getSessionUserInfo(String sessionId) {
        return stringHashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }

    // sessionId 삭제
    public void removeUserEnterInfo(String sessionId) {
        stringHashOpsEnterInfo.delete(ENTER_INFO, sessionId);
    }

    // inOutKey로 현재 유저가 접속 중인지 설정
    public void setUserChatRoomInOut(String key, Boolean inOut) {
        userInOutOperations.set(USER_INOUT + "_" + key, inOut);
    }

    // inOutKey로 현재 유저가 접속 중인지 가져오기
    public Boolean getUserChatRoomInOut(Long roomId, String name) {
        return Optional.ofNullable(userInOutOperations.get(USER_INOUT + "_" + roomId + "_" + name)).orElse(false);
    }

    // 채팅방에서 아이템 사용시 등록
    public void setItem(String roomId, String item) {
        stringHashOpsItemInfo.put(ENTER_INFO, roomId, item);
    }

    public String getItem(String roomId) {
        return stringHashOpsItemInfo.get(ENTER_INFO, roomId);
    }

    public void removeItem(String roomId) {
        stringHashOpsItemInfo.delete(ENTER_INFO, roomId);
    }
}
