package com.ssl.note.service;

import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.dto.PassengerUser;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.dto.TokenResult;
import com.ssl.note.remote.ServicePassengerUserClient;
import com.ssl.note.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author SongShengLin
 * @date 2022/11/19 12:53
 * @description
 */
@Service
@Slf4j
public class UserService {

    @Autowired
    private ServicePassengerUserClient servicePassengerUserClient;

    /**
     * 获取用户信息
     * 手机号登信息可以从Token中取得
     */
    public ResponseResult<PassengerUser> getUserByAccessToken(String accessToken) {
        log.info("accessToken:{}", accessToken);
        // 1.解析accessToken，获取用户手机号
        TokenResult tokenResult = JwtUtils.checkToken(accessToken);
        if (Objects.isNull(tokenResult)) {
            return ResponseResult.fail(CommonStatusEnum.TOKEN_ERROR.getCode(), CommonStatusEnum.TOKEN_ERROR.getMessage());
        }

        String phone = tokenResult.getPhone();
        // 2.调用用户服务，根据用户手机号获取用户信息
        ResponseResult<PassengerUser> userResponseResult = servicePassengerUserClient.getUserByPhone(phone);
        // 3.用户不存在或者服务失败，返回失败
        if (Objects.isNull(userResponseResult)
                || !Objects.equals(userResponseResult.getCode(), CommonStatusEnum.SUCCESS.getCode())
                || Objects.isNull(userResponseResult.getData())) {
            return ResponseResult.fail(CommonStatusEnum.USER_NOT_EXISTS.getCode(), CommonStatusEnum.USER_NOT_EXISTS.getMessage());
        }
        // 4.封装返回用户昵称和头像
        PassengerUser user = userResponseResult.getData();
        PassengerUser passengerUser = PassengerUser.builder()
                .passengerName(user.getPassengerName())
                .profilePhoto(user.getProfilePhoto())
                .build();
        return ResponseResult.success(passengerUser);
    }

}
