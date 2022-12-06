package com.ssl.note.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/23 11:13
 * @Describe:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceRule {

    private String cityCode;

    private String vehicleType;

    private Double startFare;

    private Integer startMile;

    private Double unitPricePerMile;

    private Double unitPricePerMinute;

    /**
     * 版本，默认1，修改往上增。
     */
    private Integer fareVersion;

    /**
     * 运价类型编码
     */
    private String fareType;

}
