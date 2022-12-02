package com.ssl.note.service;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.request.VerificationCodeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/02 20:45
 * @Describe:
 */
@Service
@Slf4j
public class VerificationCodeService {


    public ResponseResult<String> checkAndSendVerificationCode(VerificationCodeDTO verificationCodeDTO) {
        log.info("请求参数:{}", verificationCodeDTO);
        // 手机号是否存在

        // 获取验证码

        // 调用第三方发送验证码

        // 存入redis

        return ResponseResult.success("");
    }
}
