package com.ssl.note.service;

import com.ssl.note.dto.PassengerUser;
import com.ssl.note.dto.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author SongShengLin
 * @date 2022/11/19 12:53
 * @description
 */
@Service
@Slf4j
public class UserService {

    /**
     * 获取用户信息
     * 手机号登信息可以从Token中取得
     */
    public ResponseResult<PassengerUser> getUserByAccessToken(String accessToken) {
        log.info("accessToken:{}", accessToken);

        PassengerUser passengerUser = PassengerUser.builder()
                .passengerName("张三")
                .profilePhoto("头像")
                .build();
        return ResponseResult.success(passengerUser);
    }

}
