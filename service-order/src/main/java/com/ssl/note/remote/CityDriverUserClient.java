package com.ssl.note.remote;


import com.ssl.note.dto.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service-drive-user", contextId = "service-drive-user-CityDriverUserClient")
public interface CityDriverUserClient {
    @GetMapping("/city-driver/is-available-driver")
    ResponseResult<Boolean> isAvailableDriver(@RequestParam("cityCode") String cityCode);
}
