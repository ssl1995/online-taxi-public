package com.ssl.note.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    /**
     * 订单ID
     */
    private String orderId;

    // 乘客ID
    private String passengerId;

    // 乘客手机号
    private String passengerPhone;

    // 下单行政区域
    private String address;
    // 出发时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime departTime;
    // 下单时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;
    // 出发地址
    private String departure;
    // 出发经度
    private String depLongitude;
    // 出发纬度
    private String depLatitude;
    // 目的地地址
    private String destination;
    // 目的地经度
    private String destLongitude;
    // 目的地纬度
    private String destLatitude;
    // 坐标加密标识 1:gcj-02,2:wgs84,3:bd-09,4:cgcs2000北斗 0：其他
    private Integer encrypt;
    // 运价类型编码
    private String fareType;
    // 运价版本
    private Integer fareVersion;

    // 请求设备唯一码
    private String deviceCode;

    /**
     * 司机去接乘客出发时间,其实是不用传的，后端自己生成
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime toPickUpPassengerTime;

    /**
     * 去接乘客时，司机的经度
     */
    private String toPickUpPassengerLongitude;

    /**
     * 去接乘客时，司机的纬度
     */
    private String toPickUpPassengerLatitude;

    /**
     * 去接乘客时，司机的地点
     */
    private String toPickUpPassengerAddress;

    /**
     * 接到乘客，乘客上车经度
     */
    private String pickUpPassengerLongitude;

    /**
     * 接到乘客，乘客上车纬度
     */
    private String pickUpPassengerLatitude;

    /**
     * 乘客下车经度
     */
    private String passengerGetoffLongitude;

    /**
     * 乘客下车纬度
     */
    private String passengerGetoffLatitude;

    /**
     * 车型
     */
    private String vehicleType;


}