package com.ssl.note.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * @author SongShengLin
 * @date 2022/11/18 21:17
 * @description
 */
public class JwtUtils {

    public static final String SIGN = "CPFmsb!@$$";

    public static final String JWT_KEY = "passenger_phone";

    /**
     * 生成Token
     */
    public static String generatorToken(String passengerPhone) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        Date date = calendar.getTime();

        HashMap<String, String> map = new HashMap<>();
        map.put(JWT_KEY, passengerPhone);

        // 导入依赖，使用JWT构造器
        JWTCreator.Builder builder = JWT.create();
        // 传入map，过早jwt参数
        map.forEach(builder::withClaim);
        // 设置过期时间为1天
        builder.withExpiresAt(date);
        // 使用HMAC256+盐值，加密生成Token
        return builder.sign(Algorithm.HMAC256(SIGN));
    }

    /**
     * 解密Token
     */
    public static String parseToken(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(SIGN))
                .build().verify(token);
        Claim claim = decodedJWT.getClaim(JWT_KEY);
        return claim.toString();
    }

}
