package com.ssl.note.remote;

import com.ssl.note.dto.Car;
import com.ssl.note.dto.DriverCarBindingRelationship;
import com.ssl.note.dto.DriverUser;
import com.ssl.note.dto.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/26 16:41
 * @Describe:
 */
@FeignClient(name = "service-drive-user",contextId = "api-boss")
public interface DriverUserClient {

    @PostMapping("/user")
    ResponseResult<String> addUser(@RequestBody DriverUser driverUser);

    @PutMapping("/user")
    ResponseResult<String> updateUser(@RequestBody DriverUser driverUser);

    @PostMapping("/car")
    ResponseResult<String> saveCar(@RequestBody Car car);

    @PostMapping("/dirver-car-binding-relationship/bind")
    ResponseResult<String> bind(@RequestBody DriverCarBindingRelationship driverCarBindingRelationship);

    @PostMapping("/dirver-car-binding-relationship/unbind")
    ResponseResult<String> unbind(@RequestBody DriverCarBindingRelationship driverCarBindingRelationship);

}
