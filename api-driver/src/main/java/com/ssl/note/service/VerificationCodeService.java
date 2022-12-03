package com.ssl.note.service;

import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.constant.DriverCarConstants;
import com.ssl.note.constant.IdentityConstant;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.ServiceDriverUserClient;
import com.ssl.note.remote.ServiceVerificationCodeClient;
import com.ssl.note.request.VerificationCodeDTO;
import com.ssl.note.response.DriverUserExistsResponse;
import com.ssl.note.response.NumberCodeResponse;
import com.ssl.note.utils.RedisPrefixUtils;
import lombok.extern.slf4j.Slf4j;
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

        return ResponseResult.success("");
    }
}
