package com.ssl.note.remote;


import com.ssl.note.dto.ResponseResult;
import com.ssl.note.request.OrderRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "service-order",contextId = "service-pay-ServiceOrderClient")
public interface ServiceOrderClient {

    @PostMapping("/order/pay")
    ResponseResult<String> pay(@RequestBody OrderRequest orderRequest);
}
