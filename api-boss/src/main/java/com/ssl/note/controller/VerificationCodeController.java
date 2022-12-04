package com.ssl.note.controller;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.request.VerificationCodeDTO;
import com.ssl.note.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/02 20:45
 * @Describe:
 */
@RestController
public class VerificationCodeController {


    @Autowired
    private VerificationCodeService verificationCodeService;

    @PostMapping("/verification-code")
    public ResponseResult<String> checkAndSendVerificationCode(@RequestBody VerificationCodeDTO verificationCodeDTO) {
        return verificationCodeService.checkAndSendVerificationCode(verificationCodeDTO);
    }

}
