package com.ssl.note.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/21 09:02
 * @Describe:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastPriceDTO {

    private String depLongitude;

    private String depLatitude;

    private String destLongitude;

    private String destLatitude;

    private String cityCode;

    private String vehicleType;
}
