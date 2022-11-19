package com.ssl.note.response;

import lombok.Data;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/17 09:27
 * @Describe:
 */
@Data
public class TokenResponse {

    private String accessToken;

    private String refreshToken;
}
