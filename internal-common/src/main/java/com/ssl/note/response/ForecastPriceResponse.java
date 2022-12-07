package com.ssl.note.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/21 09:20
 * @Describe:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForecastPriceResponse {

    private Double price;

    private String cityCode;

    private String vehicleType;

    private String fareType;

    private Integer fareVersion;
}
