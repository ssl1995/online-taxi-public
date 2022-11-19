package com.ssl.note.remote;

import com.ssl.note.dto.PassengerUser;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.request.VerificationCodeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/17 08:32
 * @Describe:
 */
@FeignClient("service-passenger-user")
public interface ServicePassengerUserClient {

    @PostMapping("/user")
    ResponseResult loginOrRegUser(@RequestBody VerificationCodeDTO verificationCodeDTO);

    @GetMapping("/user/{phone}")
    ResponseResult<PassengerUser> getUserByPhone(@PathVariable("phone") String phone);

}
