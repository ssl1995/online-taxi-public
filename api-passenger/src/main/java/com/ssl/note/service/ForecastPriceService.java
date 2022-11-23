package com.ssl.note.service;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.ServicePriceClient;
import com.ssl.note.request.ForecastPriceDTO;
import com.ssl.note.response.ForecastPriceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/21 09:20
 * @Describe:
 */
@Service
@Slf4j
public class ForecastPriceService {

    @Autowired
    private ServicePriceClient servicePriceClient;

    /**
     * 获取预估价格
     */
    public ResponseResult<ForecastPriceResponse> forecastPrice(String depLongitude, String depLatitude, String destLongitude, String destLatitude) {
        ForecastPriceDTO req = ForecastPriceDTO.builder()
                .depLongitude(depLongitude)
                .depLatitude(depLatitude)
                .destLongitude(destLongitude)
                .destLatitude(destLatitude)
                .build();

        return servicePriceClient.forecastPrice(req);
    }
}
