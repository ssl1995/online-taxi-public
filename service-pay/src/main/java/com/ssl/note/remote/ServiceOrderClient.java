package com.ssl.note.remote;


import com.ssl.note.dto.ResponseResult;
import com.ssl.note.request.OrderRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "service-order",contextId = "service-pay-ServiceOrderClient")
public interface ServiceOrderClient {

    @RequestMapping(method = RequestMethod.POST, value = "/order/pay")
    ResponseResult pay(@RequestBody OrderRequest orderRequest);
}
