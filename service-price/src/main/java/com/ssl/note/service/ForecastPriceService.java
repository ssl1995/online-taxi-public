package com.ssl.note.service;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.ServiceMapClient;
import com.ssl.note.request.ForecastPriceDTO;
import com.ssl.note.response.DirectionResponse;
import com.ssl.note.response.ForecastPriceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * @Author: SongShengLin
 * @Date: 2022/11/21 09:44
 * @Describe:
 */
@Service
@Slf4j
public class ForecastPriceService {

    @Autowired
    private ServiceMapClient serviceMapClient;

    /**
     * 获取预估价格
     */
    public ResponseResult<ForecastPriceResponse> forecastPrice(String depLongitude, String depLatitude, String destLongitude, String destLatitude) {
        ForecastPriceDTO forecastPriceDTO = ForecastPriceDTO.builder()
                .depLongitude(depLongitude)
                .depLatitude(depLatitude)
                .destLongitude(destLongitude)
                .destLatitude(destLatitude)
                .build();
        // 获取距离和时长
        ResponseResult<DirectionResponse> directionResp = serviceMapClient.dirving(forecastPriceDTO);
        DirectionResponse directionData = directionResp.getData();

        Integer distance = directionData.getDistance();
        Integer duration = directionData.getDuration();

        ForecastPriceResponse resp = new ForecastPriceResponse();
        resp.setPrice(12.13);
        return ResponseResult.success(resp);
    }
}
