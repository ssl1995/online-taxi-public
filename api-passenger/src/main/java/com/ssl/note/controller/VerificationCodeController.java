package com.ssl.note.controller;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.VerificationCodeClient;
import com.ssl.note.request.VerificationCodeDTO;
import com.ssl.note.response.NumberCodeResponse;
import com.ssl.note.response.TokenResponse;
import com.ssl.note.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author SongShengLin
 * @date 2022/11/14 22:16
 * @description
 */
@RestController
public class VerificationCodeController {

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private VerificationCodeClient verificationCodeClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public static final String VERIFICATION_CODE_PREFIX = "passenger-verification-code-";
    public static final Integer NUMBER_SIZE = 6;

    /**
     * 获取随机6位的验证码
     */
    @GetMapping("/verification-code")
    public ResponseResult<String> getVerificationCode(@RequestBody VerificationCodeDTO verificationCodeDTO) {
        String passengerPhone = verificationCodeDTO.getPassengerPhone();

        // 获取6位随机验证码
        ResponseResult<NumberCodeResponse> numberCodeResponse = verificationCodeClient.getNumberCode(NUMBER_SIZE);
        // 存入redis
        String numberCodeValue = numberCodeResponse.getData().getNumberCode() + "";
        String key = VERIFICATION_CODE_PREFIX + numberCodeValue;
        stringRedisTemplate.opsForValue().set(key, numberCodeValue, 2L, TimeUnit.MINUTES);

        // todo 通过短信服务商，将对应的验证码发送到手机上，阿里云短信服务、腾讯短信通、华信等

        System.out.println("api服务收到: " + numberCodeValue);

        return verificationCodeService.getVerificationCode(passengerPhone);
    }


    /**
     * 校验手机号和验证码
     *
     * @param verificationCodeDTO 请求参数
     * @return 令牌Token
     */
    @PostMapping("/verification-code-check")
    public ResponseResult<TokenResponse> getVerificationCodeCheck(@RequestBody VerificationCodeDTO verificationCodeDTO) {

        return verificationCodeService.checkCode(verificationCodeDTO.getPassengerPhone(), verificationCodeDTO.getVerificationCode());
    }
}
