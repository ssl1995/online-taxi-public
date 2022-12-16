package com.ssl.note.utils;

public class SsePrefixUtils {

    public static final String operator = "$";

    public static String generatorSseKey(Long userId, String identity) {
        return userId + operator + identity;
    }
}
