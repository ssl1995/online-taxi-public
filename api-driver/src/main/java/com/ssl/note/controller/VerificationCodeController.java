package com.ssl.note.controller;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.request.VerificationCodeDTO;
import com.ssl.note.service.VerificationCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/03 10:12
 * @Describe:
 */
@RestController
@Slf4j
public class VerificationCodeController {

    @Autowired
    VerificationCodeService verificationCodeService;

    @GetMapping("/verification-code")
    public ResponseResult verificationCode(@RequestBody VerificationCodeDTO verificationCodeDTO) {
        String driverPhone = verificationCodeDTO.getDriverPhone();
        log.info("司机的号码：" + driverPhone);
        return verificationCodeService.checkAndSendVerificationCode(driverPhone);
    }

}
