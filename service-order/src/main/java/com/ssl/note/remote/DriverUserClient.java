package com.ssl.note.remote;


import com.ssl.note.dto.Car;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.response.OrderDriverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service-drive-user", contextId = "service-drive-user-CityDriverUserClient")
public interface DriverUserClient {
    @GetMapping("/city-driver/is-available-driver")
    ResponseResult<Boolean> isAvailableDriver(@RequestParam("cityCode") String cityCode);

    @GetMapping("/get-available-driver/{carId}")
    ResponseResult<OrderDriverResponse> getAvailableDriverByCarId(@PathVariable("carId") Long carId);

    @GetMapping("/car")
    ResponseResult<Car> getCarById(@RequestParam("carId") Long carId);
}
