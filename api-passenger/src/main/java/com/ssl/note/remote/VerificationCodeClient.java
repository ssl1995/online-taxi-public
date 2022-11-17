package com.ssl.note.remote;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.response.NumberCodeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/17 08:32
 * @Describe:
 */
@FeignClient("service-verificationcode")
public interface VerificationCodeClient {

    @GetMapping("/numberCode/{size}")
    ResponseResult<NumberCodeResponse> getNumberCode(@PathVariable("size") Integer size);

}
