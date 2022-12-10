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

    PRICE_RULE_EXISTS(1301,"计价规则已存在，不允许添加"),

    PRICE_RULE_NOT_EDIT(1302,"计价规则没有变化"),

    PRICE_RULE_CHANGED(1303,"计价规则有变化,请使用最新计价规则"),

    /**
     * 计价规则错误类提示，1400-1499
     */
    MAP_DISTRICT_ERROR(1400, "调用行政区域错误"),
    /**
     * 司机和车辆：1500-1599
     */
    DRIVER_CAR_BIND_NOT_EXISTS(1500, "司机和车辆绑定关系不存在"),

    DRIVER_NOT_EXIST(1501, "司机不存在"),

    DRIVER_CAR_BIND_EXISTS(1502, "司机和车辆绑定关系已存在，请勿重复绑定"),

    DRIVER_BIND_EXISTS(1503, "司机已经被绑定了，请勿重复绑定"),

    CAR_BIND_EXISTS(1504, "车辆已经被绑定了，请勿重复绑定"),

    CITY_DRIVER_EMPTY(1505, "当前城市没有可用的司机"),

    AVAILABLE_DRIVER_EMPTY(1506, "可用的司机为空"),

    /**
     * 订单：1600-1699
     */
    ORDER_GOING_ON(1600,"有正在进行的订单"),

    /**
     * 下单异常
     */
    DEVICE_IS_BLACK(1601,"拒绝下单,该设备超过下单次数"),

    CITY_SERVICE_NOT_SERVICE(1602,"当前城市不提供叫车服务"),

    ORDER_CANCEL_ERROR(1603, "订单取消失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(3000, "请求参数错误"),

    PARAM_EMPTY_ERROR(3001, "请求参数不能为空"),

    ;
    private Integer code;
    private String message;
}


