package com.ssl.note.remote;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.request.OrderRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/05 16:59
 * @Describe:
 */
@FeignClient(name = "service-order", contextId = "api-passenger-ServiceOrderClient")
public interface ServiceOrderClient {
    @PostMapping("/order/add")
    ResponseResult<String> add(@RequestBody OrderRequest orderRequest);

    @GetMapping("/test/order/add/{orderId}")
    String orderAddTerminalTest(@PathVariable("orderId") Long orderId);

    /**
     * 取消订单
     */
    @PostMapping("/order/cancel")
    ResponseResult<String> cancel(@RequestParam("orderId") String orderId, @RequestParam("identity") String identity);

}
