package com.ssl.note.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDriverResponse {

    /**
     * 司机Id
     */
    private Long driverId;
    /**
     * 司机手机号
     */
    private String driverPhone;

    /**
     * 车辆Id
     */
    private Long carId;

    /**
     * 机动车驾驶证号
     */
    private String licenseId;

    /**
     * 车辆号牌
     */
    private String vehicleNo;

    /**
     * 车辆类型
     */
    private String vehicleType;
}
