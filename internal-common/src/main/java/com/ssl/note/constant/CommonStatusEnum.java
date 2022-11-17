package com.ssl.note.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author SongShengLin
 * @date 2022/11/16 23:21
 * @description
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CommonStatusEnum {

    SUCCESS(1, "success"),
    FAIL(0, "fail"),

    VERIFICATION_CODE_ERROR(1099, "验证码不存在/不正确"),
    ;

    private Integer code;
    private String message;
}


