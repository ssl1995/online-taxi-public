package com.ssl.note.remote;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.request.OrderRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service-order", contextId = "api-driver-service-map-ServiceOrderClient")
public interface ServiceOrderClient {

    /**
     * 去接乘客
     */
    @PostMapping("/order/to-puck-up-passenger")
    ResponseResult<String> toPuckUpPassenger(@RequestBody OrderRequest request);

    /**
     * 到达乘客上车点
     */
    @PostMapping("/order/arrived-departure")
    ResponseResult<String> arrivedDeparture(@RequestBody OrderRequest orderRequest);

    /**
     * 司机接到乘客
     */
    @PostMapping("/order/puck-up-passenger")
    ResponseResult<String> pucUpPassenger(@RequestBody OrderRequest request);

    /**
     * 乘客到达目的地，下车，行程终止
     */
    @PostMapping("/order/passenger-getoff")
    ResponseResult<String> passengerGetOff(@RequestBody OrderRequest orderRequest);

    /**
     * 取消订单
     */
    @PostMapping("/order/cancel")
    ResponseResult<String> cancel(@RequestParam("orderId") String orderId, @RequestParam("identity") String identity);

    /**
     * 修改支付状态为发起收款
     */
    @PostMapping("/order/push-pay-info")
    ResponseResult<String> pushPayInfo(@RequestBody OrderRequest orderRequest);

}
