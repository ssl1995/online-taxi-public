package com.ssl.note.remote;

import com.ssl.note.dto.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/07 22:26
 * @Describe:
 */
@FeignClient(name = "service-price", contextId = "service-order-ServicePriceClient")
public interface ServicePriceClient {

    @GetMapping("/price-rule/is-new")
    ResponseResult<Boolean> isNew(@RequestParam("fareType") String fareType, @RequestParam("fareVersion") Integer fareVersion);
}
