package com.ssl.note.controller;

import com.ssl.note.dto.OrderInfo;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.request.OrderRequest;
import com.ssl.note.service.OrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderInfoService;

    /**
     * 下订单
     */
    @PostMapping("/add")
    public ResponseResult<String> add(@RequestBody OrderRequest orderRequest) {
        log.info("service-order接收到请求参数:{}", orderRequest);
        return orderInfoService.add(orderRequest);
    }

    /**
     * 查询订单
     */
    @GetMapping("/{id}")
    public ResponseResult<OrderInfo> getById(@PathVariable("id") Long id) {
        return orderInfoService.getById(id);
    }

    /**
     * 去接乘客
     */
    @PostMapping("/to-puck-up-passenger")
    public ResponseResult<String> toPuckUpPassenger(@RequestBody OrderRequest request) {
        return orderInfoService.toPuckUpPassenger(request);
    }

    /**
     * 到达乘客上车点
     */
    @PostMapping("/arrived-departure")
    public ResponseResult<String> arrivedDeparture(@RequestBody OrderRequest orderRequest){
        return orderInfoService.arrivedDeparture(orderRequest);
    }

    /**
     * 司机接到乘客
     */
    @PostMapping("/puck-up-passenger")
    public ResponseResult<String> pucUpPassenger(@RequestBody OrderRequest request) {
        return orderInfoService.puckUpPassenger(request);
    }


    /**
     * 乘客到达目的地，下车，行程终止
     */
    @PostMapping("/passenger-getoff")
    public ResponseResult<String> passengerGetOff(@RequestBody OrderRequest orderRequest){
        return orderInfoService.passengerGetOff(orderRequest);
    }

    /**
     * 支付成功
     */
    @PostMapping("/pay")
    public ResponseResult<String> pay(@RequestBody OrderRequest orderRequest){
        return orderInfoService.pay(orderRequest);
    }
}
