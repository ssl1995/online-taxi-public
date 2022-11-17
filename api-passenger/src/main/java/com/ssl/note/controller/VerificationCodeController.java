package com.ssl.note.controller;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.VerificationCodeClient;
import com.ssl.note.request.VerificationCodeDTO;
import com.ssl.note.response.NumberCodeResponse;
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

    @Autowired
    private VerificationCodeClient verificationCodeClient;

    @GetMapping("/verification-code")
    public String getVerificationCode(@RequestBody VerificationCodeDTO verificationCodeDTO) {
        String passengerPhone = verificationCodeDTO.getPassengerPhone();

        ResponseResult<NumberCodeResponse> numberCodeResponse = verificationCodeClient.getNumberCode();
        Integer numberCode = numberCodeResponse.getData().getNumberCode();

        System.out.println("api服务收到: "+numberCode);

        return verificationCodeService.getVerificationCode(passengerPhone);
    }
}
