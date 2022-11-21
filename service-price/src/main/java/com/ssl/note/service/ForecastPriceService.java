package com.ssl.note.service;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.response.ForecastPriceResponse;
import org.springframework.stereotype.Service;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/21 09:44
 * @Describe:
 */
@Service
public class ForecastPriceService {

    /**
     * 获取预估价格
     */
    public ResponseResult<ForecastPriceResponse> forecastPrice(String depLongitude, String depLatitude, String destLongitude, String destLatitude) {

        ForecastPriceResponse resp = new ForecastPriceResponse();
        resp.setPrice(12.13);
        return ResponseResult.success(resp);
    }
}
