package com.ssl.note.controller;

import com.ssl.note.dto.DicDistrict;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.mapper.DictDistrictMapper;
import com.ssl.note.request.ForecastPriceDTO;
import com.ssl.note.response.DirectionResponse;
import com.ssl.note.service.DirectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/21 11:19
 * @Describe:
 */
@RestController
@RequestMapping("/direction")
public class DirectionController {


    @Autowired
    private DirectionService directionService;

    @GetMapping("/driving")
    public ResponseResult<DirectionResponse> dirving(@RequestBody ForecastPriceDTO forecastPriceDTO) {
        String depLongitude = forecastPriceDTO.getDepLongitude();
        String depLatitude = forecastPriceDTO.getDepLatitude();
        String destLongitude = forecastPriceDTO.getDestLongitude();
        String destLatitude = forecastPriceDTO.getDestLatitude();

        return directionService.dirving(depLongitude, depLatitude, destLongitude, destLatitude);
    }

}
