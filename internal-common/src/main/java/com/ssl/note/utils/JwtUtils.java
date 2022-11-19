package com.ssl.note.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ssl.note.dto.TokenResult;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * @author SongShengLin
 * @date 2022/11/18 21:17
 * @description
 */
public class JwtUtils {

    /**
     * 盐值
     */
    public static final String SIGN = "CPFmsb!@$$";

    public static final String JWT_KEY_PHONE = "passenger_phone";

    /**
     * 身份校验，乘客是1，司机是2
     */
    public static final String JWT_KEY_IDENTITY = "identity";

    /**
     * token的类型，便于区分双token
     */
    public static final String JWT_TOKEN_TYPE = "token-type";


    /**
     * 生成Token
     */
    public static String generatorToken(String passengerPhone, String identity, String tokenType) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        Date date = calendar.getTime();

        HashMap<String, String> map = new HashMap<>();
        map.put(JWT_KEY_PHONE, passengerPhone);
        map.put(JWT_KEY_IDENTITY, identity);
        map.put(JWT_TOKEN_TYPE, tokenType);

        // 导入依赖，使用JWT构造器
        JWTCreator.Builder builder = JWT.create();
        // 传入map,设置Jwt参数
        map.forEach(builder::withClaim);
        // 设置过期时间为1天，后续使用Redis存储过期时间
//        builder.withExpiresAt(date);
        // 使用HMAC256+盐值，加密生成Token
        return builder.sign(Algorithm.HMAC256(SIGN));
    }

    /**
     * 解密Token
     */
    public static TokenResult parseToken(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(SIGN))
                .build().verify(token);
        String phone = decodedJWT.getClaim(JWT_KEY_PHONE).asString();
        String identity = decodedJWT.getClaim(JWT_KEY_IDENTITY).asString();
        return TokenResult.builder().phone(phone).identity(identity).build();
    }

    public static void main(String[] args) {
        String phone = "13639120050";
        String identity = "1";
//        System.out.println(generatorToken(phone, identity));
    }

}
