package com.ssl.note.controller;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.request.VerificationCodeDTO;
import com.ssl.note.response.TokenResponse;
import com.ssl.note.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SongShengLin
 * @date 2022/11/14 22:16
 * @description
 */
@RestController
public class VerificationCodeController {

    @Autowired
    private VerificationCodeService verificationCodeService;

    /**
     * 用户获取随机6位的验证码
     */
    @GetMapping("/verification-code")
    public ResponseResult<String> getVerificationCode(@RequestBody VerificationCodeDTO verificationCodeDTO) {
        String passengerPhone = verificationCodeDTO.getPassengerPhone();

        return verificationCodeService.generatorCode(passengerPhone);
    }

    /**
     * 用户校验手机号和验证码
     *
     * @param verificationCodeDTO 请求参数
     * @return 令牌Token
     */
    @PostMapping("/verification-code-check")
    public ResponseResult<TokenResponse> getVerificationCodeCheck(@RequestBody VerificationCodeDTO verificationCodeDTO) {

        return verificationCodeService.checkCode(verificationCodeDTO.getPassengerPhone(), verificationCodeDTO.getVerificationCode());
    }
}
