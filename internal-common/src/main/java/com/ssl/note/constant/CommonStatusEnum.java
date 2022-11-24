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

    /**
     * 成功
     */
    SUCCESS(1, "success"),

    /**
     * 失败
     */
    FAIL(0, "fail"),

    /**
     * 验证码错误提示，1000-1099
     */
    VERIFICATION_CODE_ERROR(1099, "验证码不存在/不正确"),

    /**
     * Token错误类提示，1100-1199
     */
    TOKEN_ERROR(1199, "Token过期或不存在"),

    /**
     * 用户错误类提示，1200-1299
     */
    USER_NOT_EXISTS(1200, "用户不存在"),

    /**
     * 计价规则错误类提示，1300-1399
     */
    PRICE_RULE_EMPTY(1300, "计价规则不存在"),

    /**
     * 计价规则错误类提示，1400-1499
     */
    MAP_DISTRICT_ERROR(1400, "调用行政区域错误"),
    ;
    private Integer code;
    private String message;
}


