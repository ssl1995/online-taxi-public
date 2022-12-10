package com.ssl.note.constant;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/01 18:20
 * @Describe:
 */
public class DriverCarConstants {

    /**
     * DriverCarBindingRelationship:绑定车辆
     */
    public static final Integer DRIVER_CAR_BIND = 1;

    /**
     * DriverCarBindingRelationship:解绑车辆
     */
    public static final Integer DRIVER_CAR_UNBIND = 2;

    /**
     * 司机有效
     */
    public static final Integer DRIVER_STATE_VALID = 1;

    /**
     * 司机无效
     */
    public static final Integer DRIVER_STATE_INVALID = 0;


    /**
     * 司机存在
     */
    public static final Integer DRIVER_EXISTS = 1;

    /**
     * 司机不存在
     */
    public static final Integer DRIVER_NOT_EXISTS = 0;


    /**
     * driverUserWorkStatus：司机工作状态：0=收车
     */
    public static final Integer DRIVER_WORK_STATUS_STOP = 0;

    /**
     * 司机工作状态：1=出车
     */
    public static final Integer DRIVER_WORK_STATUS_START = 1;

    /**
     * 司机工作状态：2=暂停
     */
    public static final Integer DRIVER_WORK_STATUS_SUSPEND = 2;
}
