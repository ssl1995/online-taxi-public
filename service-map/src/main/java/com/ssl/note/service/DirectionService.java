package com.ssl.note.service;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.request.ForecastPriceDTO;
import com.ssl.note.response.DirectionResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/21 11:24
 * @Describe:
 */
@Service
public class DirectionService {

    public ResponseResult<DirectionResponse> dirving(String depLongitude, String depLatitude, String destLongitude, String destLatitude) {

        DirectionResponse directionResponse = DirectionResponse
                .builder()
                .distance(13)
                .duration(14)
                .build();

        return ResponseResult.success(directionResponse);
    }
}
