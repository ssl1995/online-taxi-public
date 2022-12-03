package com.ssl.note.remote;

import com.ssl.note.dto.DriverUser;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.response.DriverUserExistsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/26 16:41
 * @Describe:
 */
@FeignClient(name = "service-drive-user",contextId = "api-driver-service-drive-user")
public interface ServiceDriverUserClient {

    @PostMapping("/user")
    ResponseResult<String> addUser(@RequestBody DriverUser driverUser);

    @PutMapping("/user")
    ResponseResult<String> updateUser(@RequestBody DriverUser driverUser);

    @GetMapping("/check-driver/{driverPhone}")
    ResponseResult<DriverUserExistsResponse> checkDriver(@PathVariable("driverPhone") String driverPhone);

}
