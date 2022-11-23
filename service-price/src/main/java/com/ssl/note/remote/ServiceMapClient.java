package com.ssl.note.remote;


import com.ssl.note.dto.ResponseResult;
import com.ssl.note.request.ForecastPriceDTO;
import com.ssl.note.response.DirectionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "service-map")
public interface ServiceMapClient {

    @GetMapping("/direction/driving")
    ResponseResult<DirectionResponse> dirving(@RequestBody ForecastPriceDTO forecastPriceDTO);

}
