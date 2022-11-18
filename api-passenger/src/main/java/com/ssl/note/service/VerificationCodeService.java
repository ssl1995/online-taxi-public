package com.ssl.note.service;

import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.ServicePassengerUserClient;
import com.ssl.note.remote.ServiceVerificationCodeClient;
import com.ssl.note.request.VerificationCodeDTO;
import com.ssl.note.response.NumberCodeResponse;
import com.ssl.note.response.TokenResponse;
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

    public static final String VERIFICATION_CODE_PREFIX = "passenger-verification-code-";
    public static final Integer NUMBER_SIZE = 6;


    public ResponseResult<String> generatorCode(String passengerPhone) {
        // 获取6位随机验证码
        ResponseResult<NumberCodeResponse> numberCodeResponse = verificationCodeClient.getNumberCode(NUMBER_SIZE);
        // 存入redis
        String key = generateKeyByPhone(passengerPhone);
        String value = numberCodeResponse.getData().getNumberCode() + "";
        stringRedisTemplate.opsForValue().set(key, value, 2L, TimeUnit.MINUTES);

        // todo 通过短信服务商，将对应的验证码发送到手机上，阿里云短信服务、腾讯短信通、华信等

        return ResponseResult.success(value);
    }

    //
    private String generateKeyByPhone(String phone) {
        return VERIFICATION_CODE_PREFIX + phone;
    }

    public ResponseResult<TokenResponse> checkCode(String passengerPhone, String numberCode) {

        // 从redis取出存过的验证码
        String key = generateKeyByPhone(passengerPhone);
        String valueRedis = stringRedisTemplate.opsForValue().get(key);

        // 验证码不存在
        if (StringUtils.isBlank(valueRedis) || !StringUtils.equals(valueRedis, numberCode.trim())) {
            return ResponseResult.fail(CommonStatusEnum.VERIFICATION_CODE_ERROR.getCode(), CommonStatusEnum.VERIFICATION_CODE_ERROR.getMessage());
        }

        // 验证码存在
        // 登录或注册用户
        servicePassengerUserClient.loginOrRegUser(VerificationCodeDTO.builder().passengerPhone(passengerPhone).build());

        // 生成令牌，给到用户，方便多次登录
        TokenResponse token = new TokenResponse();
        token.setToken("abcdef");
        return ResponseResult.success(token);
    }
}
