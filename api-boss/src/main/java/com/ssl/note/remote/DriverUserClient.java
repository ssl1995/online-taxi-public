package com.ssl.note.remote;

import com.ssl.note.dto.DriverUser;
import com.ssl.note.dto.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/26 16:41
 * @Describe:
 */
@FeignClient("service-drive-user")
public interface DriverUserClient {

    @PostMapping("/user")
    ResponseResult<String> addUser(@RequestBody DriverUser driverUser);

    @PutMapping("/user")
    ResponseResult<String> updateUser(@RequestBody DriverUser driverUser);

}
