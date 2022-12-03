package com.ssl.note.service;

import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.constant.IdentityConstant;
import com.ssl.note.constant.TokenConstant;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.ServicePassengerUserClient;
import com.ssl.note.remote.ServiceVerificationCodeClient;
import com.ssl.note.request.VerificationCodeDTO;
import com.ssl.note.response.NumberCodeResponse;
import com.ssl.note.response.TokenResponse;
import com.ssl.note.utils.JwtUtils;
import com.ssl.note.utils.RedisPrefixUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author SongShengLin
 * @date 2022/11/14 22:17
 * @description
 */
@Service
public class VerificationCodeService {

    @Autowired
    private ServiceVerificationCodeClient verificationCodeClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ServicePassengerUserClient servicePassengerUserClient;
    public static final Integer NUMBER_SIZE = 6;


    public ResponseResult<String> generatorCode(String passengerPhone) {
        // 获取6位随机验证码
        ResponseResult<NumberCodeResponse> numberCodeResponse = verificationCodeClient.getNumberCode(NUMBER_SIZE);

        // 将随机验证码存入redis，过期时间为2分钟
        String key = RedisPrefixUtils.generateKeyByPhone(passengerPhone, IdentityConstant.PASSENGER_IDENTITY);
        String value = numberCodeResponse.getData().getNumberCode() + "";
        stringRedisTemplate.opsForValue().set(key, value, 2L, TimeUnit.MINUTES);

        // todo 通过短信服务商，将对应的验证码发送到手机上，阿里云短信服务、腾讯短信通、华信等

        return ResponseResult.success(value);
    }


    public ResponseResult<TokenResponse> checkCode(String passengerPhone, String numberCode) {

        // 1.从redis取出发送验证码时，redis存过的验证码
        String key = RedisPrefixUtils.generateKeyByPhone(passengerPhone, IdentityConstant.PASSENGER_IDENTITY);
        String valueRedis = stringRedisTemplate.opsForValue().get(key);

        // 2.验证码不存在
        if (StringUtils.isBlank(valueRedis) || !StringUtils.equals(valueRedis, numberCode.trim())) {
            return ResponseResult.fail(CommonStatusEnum.VERIFICATION_CODE_ERROR.getCode(), CommonStatusEnum.VERIFICATION_CODE_ERROR.getMessage());
        }

        // 3.验证码存在
        // 登录或注册用户
        servicePassengerUserClient.loginOrRegUser(VerificationCodeDTO.builder().passengerPhone(passengerPhone).build());

        // 4.双Token
        String accessToken = JwtUtils.generatorToken(passengerPhone, IdentityConstant.PASSENGER_IDENTITY, TokenConstant.ACCESS_TOKEN_TYPE);
        String refreshToken = JwtUtils.generatorToken(passengerPhone, IdentityConstant.PASSENGER_IDENTITY, TokenConstant.REFRESH_TOKEN_TYPE);

        String accessTokenKey = RedisPrefixUtils.generateTokenKey(passengerPhone, IdentityConstant.PASSENGER_IDENTITY, TokenConstant.ACCESS_TOKEN_TYPE);
        String refreshTokenKey = RedisPrefixUtils.generateTokenKey(passengerPhone, IdentityConstant.PASSENGER_IDENTITY, TokenConstant.REFRESH_TOKEN_TYPE);

        stringRedisTemplate.opsForValue().set(accessTokenKey, accessToken, 30, TimeUnit.DAYS);
        stringRedisTemplate.opsForValue().set(refreshTokenKey, refreshToken, 31, TimeUnit.DAYS);

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);

        return ResponseResult.success(tokenResponse);
    }

}
