package com.ssl.note.service;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.response.TokenResponse;
import org.springframework.stereotype.Service;

/**
 * @author SongShengLin
 * @date 2022/11/14 22:17
 * @description
 */
@Service
public class VerificationCodeService {

    public ResponseResult<String> getVerificationCode(String passengerPhone) {
        return ResponseResult.success(passengerPhone);
    }

    public ResponseResult<TokenResponse> checkCode(String passengerPhone, String numberCode) {


        TokenResponse token = new TokenResponse();
        token.setToken("abcdef");
        return ResponseResult.success(token);
    }
}
