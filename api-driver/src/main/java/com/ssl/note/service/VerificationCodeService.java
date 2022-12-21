package com.ssl.note.service;

import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.constant.DriverCarConstants;
import com.ssl.note.constant.IdentityConstant;
import com.ssl.note.constant.TokenConstant;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.ServiceDriverUserClient;
import com.ssl.note.remote.ServiceVerificationCodeClient;
import com.ssl.note.response.DriverUserExistsResponse;
import com.ssl.note.response.NumberCodeResponse;
import com.ssl.note.response.TokenResponse;
import com.ssl.note.utils.JwtUtils;
import com.ssl.note.utils.RedisPrefixUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/02 21:27
 * @Describe:
 */
@Service
@Slf4j
public class VerificationCodeService {

    @Autowired
    private ServiceDriverUserClient serviceDriverUserClient;

    @Autowired
    private ServiceVerificationCodeClient serviceVerificationCodeClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

//    @Autowired
//    private ServicePassengerUserClient servicePassengerUserClient;

    public static final Integer SIZE = 6;

    public ResponseResult<String> checkAndSendVerificationCode(String driverPhone) {
        // 1.手机号是否存在
        ResponseResult<DriverUserExistsResponse> driverResp = serviceDriverUserClient.checkDriver(driverPhone);
        log.info("driverResult:{}", driverResp);

        Integer ifExists = driverResp.getData().getIfExists();
        if (!Objects.equals(ifExists, DriverCarConstants.DRIVER_EXISTS)) {
            return ResponseResult.fail(CommonStatusEnum.DRIVER_NOT_EXIST.getCode(), CommonStatusEnum.DRIVER_NOT_EXIST.getMessage());
        }

        // 2.获取验证码
        ResponseResult<NumberCodeResponse> numberCodeResp = serviceVerificationCodeClient.getNumberCode(SIZE);
        Integer numberCode = numberCodeResp.getData().getNumberCode();
        log.info("numberCode:{}", numberCode);

        // 3.调用第三方发送验证码，后期加上

        // 4.存入redis
        String key = RedisPrefixUtils.generateKeyByPhone(driverPhone, IdentityConstant.DIVER_IDENTITY);
        String value = numberCode + "";
        stringRedisTemplate.opsForValue().set(key, value, 2, TimeUnit.MINUTES);

        return ResponseResult.success(String.valueOf(numberCode));
    }

    public ResponseResult<TokenResponse> checkCode(String driverPhone, String verificationCode) {
        // 1.从redis取出发送验证码时，redis存过的验证码
        String key = RedisPrefixUtils.generateKeyByPhone(driverPhone, IdentityConstant.DIVER_IDENTITY);
        String valueRedis = stringRedisTemplate.opsForValue().get(key);

        // 2.验证码不存在
        if (StringUtils.isBlank(valueRedis) || !StringUtils.equals(valueRedis, verificationCode.trim())) {
            return ResponseResult.fail(CommonStatusEnum.VERIFICATION_CODE_ERROR.getCode(), CommonStatusEnum.VERIFICATION_CODE_ERROR.getMessage());
        }

        // 3.验证码存在
        // 检查司机是否存在，在发送验证码的时候就已经验证了，这里无需验证
//        servicePassengerUserClient.loginOrRegUser(VerificationCodeDTO.builder().driverPhone(driverPhone).build());

        // 4.双Token
        String accessToken = JwtUtils.generatorToken(driverPhone, IdentityConstant.DIVER_IDENTITY, TokenConstant.ACCESS_TOKEN_TYPE);
        String refreshToken = JwtUtils.generatorToken(driverPhone, IdentityConstant.DIVER_IDENTITY, TokenConstant.REFRESH_TOKEN_TYPE);

        String accessTokenKey = RedisPrefixUtils.generateTokenKey(driverPhone, IdentityConstant.DIVER_IDENTITY, TokenConstant.ACCESS_TOKEN_TYPE);
        String refreshTokenKey = RedisPrefixUtils.generateTokenKey(driverPhone, IdentityConstant.DIVER_IDENTITY, TokenConstant.REFRESH_TOKEN_TYPE);

        stringRedisTemplate.opsForValue().set(accessTokenKey, accessToken, 30, TimeUnit.DAYS);
        stringRedisTemplate.opsForValue().set(refreshTokenKey, refreshToken, 31, TimeUnit.DAYS);

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);

        return ResponseResult.success(tokenResponse);
    }
}
