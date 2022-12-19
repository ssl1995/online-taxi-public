package com.ssl.note.controller;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.request.ForecastPriceDTO;
import com.ssl.note.response.ForecastPriceResponse;
import com.ssl.note.service.PriceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/21 09:03
 * @Describe:
 */
@RestController
@Slf4j
public class ForecastPriceController {

    @Autowired
    private PriceService forecastPriceService;


    /**
     * 获取预估价格
     */
    @PostMapping("/forecast-price")
    public ResponseResult<ForecastPriceResponse> forecastPrice(@RequestBody ForecastPriceDTO forecastPriceDTO) {
        String depLongitude = forecastPriceDTO.getDepLongitude();
        String depLatitude = forecastPriceDTO.getDepLatitude();
        String destLongitude = forecastPriceDTO.getDestLongitude();
        String destLatitude = forecastPriceDTO.getDestLatitude();
        String cityCode = forecastPriceDTO.getCityCode();
        String vehicleType = forecastPriceDTO.getVehicleType();

        return forecastPriceService.forecastPrice(depLongitude, depLatitude, destLongitude, destLatitude, cityCode, vehicleType);
    }

    /**
     * 获取实际价格
     */
    @PostMapping("/calculate-price")
    public ResponseResult<Double> calculatePrice(@RequestParam Integer distance, @RequestParam Integer duration,
                                                 @RequestParam String cityCode, @RequestParam String vehicleType) {
        return forecastPriceService.calculatePrice(distance, duration, cityCode, vehicleType);
    }
}
