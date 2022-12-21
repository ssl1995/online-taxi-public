package com.ssl.note.remote;

import com.ssl.note.dto.*;
import com.ssl.note.response.DriverUserExistsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/26 16:41
 * @Describe:
 */
@FeignClient(name = "service-drive-user", contextId = "api-driver-service-drive-user")
public interface ServiceDriverUserClient {

    @PostMapping("/user")
    ResponseResult<String> addUser(@RequestBody DriverUser driverUser);

    @PutMapping("/user")
    ResponseResult<String> updateUser(@RequestBody DriverUser driverUser);

    @GetMapping("/check-driver/{driverPhone}")
    ResponseResult<DriverUserExistsResponse> checkDriver(@PathVariable("driverPhone") String driverPhone);

    @GetMapping("/car")
    ResponseResult<Car> getCarById(@RequestParam("carId") Long carId);

    @PostMapping("/driver-user-word-status")
    ResponseResult<String> changeWorkStatus(@RequestBody DriverUserWorkStatus driverUserWorkStatus);

    @GetMapping("/driver-car-binding-relationship")
    ResponseResult<DriverCarBindingRelationship> getDriverCarBindingRelationship(@RequestParam("driverPhone") String driverPhone);
}
