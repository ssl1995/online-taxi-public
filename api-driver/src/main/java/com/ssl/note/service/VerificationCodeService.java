package com.ssl.note.service;

import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.constant.DriverCarConstants;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.ServiceDriverUserClient;
import com.ssl.note.remote.ServiceVerificationCodeClient;
import com.ssl.note.request.VerificationCodeDTO;
import com.ssl.note.response.DriverUserExistsResponse;
import com.ssl.note.response.NumberCodeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

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

        // 调用第三方发送验证码

        // 存入redis

        return ResponseResult.success("");
    }
}
