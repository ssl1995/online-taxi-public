package com.ssl.note.utils;

/**
 * @author SongShengLin
 * @date 2022/11/18 23:35
 * @description
 */
public class RedisPrefixUtils {

    public static final String VERIFICATION_CODE_PREFIX = "verification-code-";
    public static final String TOKEN_PREFIX = "token-";

    /**
     * 根据手机号生成redis的key
     *
     * @param phone    手机号
     * @param identity 身份标识
     * @return redis的key
     */
    public static String generateKeyByPhone(String phone, String identity) {
        return VERIFICATION_CODE_PREFIX + identity + "-" + phone;
    }

    // 根据手机号、用户列别生成Redis的Key
    public static String generateTokenKey(String phone, String identity, String tokenType) {
        return TOKEN_PREFIX + phone + "-" + identity + "-" + tokenType;
    }
}
