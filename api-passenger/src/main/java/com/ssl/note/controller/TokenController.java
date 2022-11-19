package com.ssl.note.controller;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.response.TokenResponse;
import com.ssl.note.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SongShengLin
 * @date 2022/11/19 11:43
 * @description
 */
@RestController
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @PostMapping("/token-refresh")
    public ResponseResult<TokenResponse> refreshToken(@RequestBody TokenResponse tokenRequest) {
        String refreshToken = tokenRequest.getRefreshToken();
        return tokenService.refreshToken(refreshToken);
    }
}
