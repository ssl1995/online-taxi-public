package com.ssl.note.service;

import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.constant.IdentityConstant;
import com.ssl.note.constant.TokenConstant;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.dto.TokenResult;
import com.ssl.note.response.TokenResponse;
import com.ssl.note.utils.JwtUtils;
import com.ssl.note.utils.RedisPrefixUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author SongShengLin
 * @date 2022/11/19 11:53
 * @description
 */
@Service
public class TokenService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    public ResponseResult<TokenResponse> refreshToken(String refreshTokenSrc) {
        // 1.解析原refreshToken，得到用户手机号和identity
        TokenResult tokenResult = JwtUtils.checkToken(refreshTokenSrc);
        if (Objects.isNull(tokenResult)) {
            return ResponseResult.fail(CommonStatusEnum.TOKEN_ERROR.getCode(), CommonStatusEnum.TOKEN_ERROR.getMessage());
        }
        String phone = tokenResult.getPhone();
        String identity = tokenResult.getIdentity();

        // 2.根据用户手机号和identity读取Redis中数据
        String refreshTokenKey = RedisPrefixUtils.generateTokenKey(phone, IdentityConstant.PASSENGER_IDENTITY, TokenConstant.REFRESH_TOKEN_TYPE);
        String refreshTokenRedis = stringRedisTemplate.opsForValue().get(refreshTokenKey);
        // 3.refreshToken已过期或不存在
        if (StringUtils.isBlank(refreshTokenRedis) || !StringUtils.equals(refreshTokenSrc.trim(), refreshTokenRedis.trim())) {
            return ResponseResult.fail(CommonStatusEnum.TOKEN_ERROR.getCode(), CommonStatusEnum.TOKEN_ERROR.getMessage());
        }

        // 4.重新生成新的双Token，并存入Redis
        String accessToken = JwtUtils.generatorToken(phone, identity, TokenConstant.ACCESS_TOKEN_TYPE);
        String refreshNewToken = JwtUtils.generatorToken(phone, identity, TokenConstant.REFRESH_TOKEN_TYPE);

        String accessTokenKey = RedisPrefixUtils.generateTokenKey(phone, IdentityConstant.PASSENGER_IDENTITY, TokenConstant.ACCESS_TOKEN_TYPE);

        stringRedisTemplate.opsForValue().set(accessTokenKey, accessToken, 30, TimeUnit.DAYS);
        stringRedisTemplate.opsForValue().set(refreshTokenKey, refreshNewToken, 31, TimeUnit.DAYS);

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshNewToken);

        return ResponseResult.success(tokenResponse);
    }
}
