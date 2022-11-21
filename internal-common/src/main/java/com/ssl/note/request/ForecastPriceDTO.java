package com.ssl.note.request;

import lombok.Data;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/21 09:02
 * @Describe:
 */
@Data
public class ForecastPriceDTO {

    private String depLongitude;

    private String depLatitude;

    private String destLongitude;

    private String destLatitude;
}
