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
    public static final String JWT_TOKEN_TYPE = "tokenType";

    /**
     * token的时间，防止生成相同的Token
     */
    public static final String JWT_TOKEN_TIME = "tokenTime";


    /**
     * 生成Token
     */
    public static String generatorToken(String passengerPhone, String identity, String tokenType) {
        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DATE, 1);
        Date curDate = calendar.getTime();

        /**
         * JWT
         * 1.标头：标识加密算法
         * 2.负载信息：需要加密进去的东西
         * 3.签名：对前2+颜值进行加密，防止篡改
         */

        // JWT需要封装的参数
        HashMap<String, String> map = new HashMap<>();
        // 用户手机号
        map.put(JWT_KEY_PHONE, passengerPhone);
        // 用户身份标识
        map.put(JWT_KEY_IDENTITY, identity);
        // token类型:是refreshToken还是accessToken
        map.put(JWT_TOKEN_TYPE, tokenType);
        // 防止重复:防止Token一样，使用当前时间进行加密
        map.put(JWT_TOKEN_TIME, curDate.toString());

        // JWT构造器:导入auth0依赖
        JWTCreator.Builder builder = JWT.create();
        // 构造器传入需要封装的参数
        map.forEach(builder::withClaim);
        // 构造器设置过期时间为1天，后续采用Redis存储过期时间
//        builder.withExpiresAt(date);
        // 构造器使用HMAC256+盐值加密，生成Token
        // todo: 点进去学习有哪些加密算法
        // HS256:对称加密 RS256和ES256:私钥加密、公钥解密
        return builder.sign(Algorithm.HMAC256(SIGN));
    }

    /**
     * 解密Token
     */
    public static TokenResult parseToken(String token) {
        // 1.解析Token:使用什么加密方式，就使用什么解密
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(SIGN)).build().verify(token);
        // 2.解密所需要的手机号和身份标识
        String phone = decodedJWT.getClaim(JWT_KEY_PHONE).asString();
        String identity = decodedJWT.getClaim(JWT_KEY_IDENTITY).asString();

        return TokenResult.builder().phone(phone).identity(identity).build();
    }

    public static TokenResult checkToken(String token) {
        TokenResult tokenResult = null;
        try {
            tokenResult = parseToken(token);
        } catch (Exception e) {

        }

//        catch (SignatureVerificationException e) {
//            failMsg = "token sign error";
//            result = false;
//        } catch (TokenExpiredException e) {
//            failMsg = "token time out";
//            result = false;
//        } catch (AlgorithmMismatchException e) {
//            failMsg = "token AlgorithmMismatchException";
//            result = false;
//        } catch (Exception e) {
//            failMsg = "token invalid";
//            result = false;
//        }
        return tokenResult;
    }

    public static void main(String[] args) {
        String phone = "13639120050";
        String identity = "1";
//        System.out.println(generatorToken(phone, identity));
    }

}
