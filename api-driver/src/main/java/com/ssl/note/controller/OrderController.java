package com.ssl.note.controller;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.request.OrderRequest;
import com.ssl.note.service.ApiOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/19 21:39
 * @Describe:
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private ApiOrderService apiOrderService;


    @PostMapping("/to-puck-up-passenger")
    public ResponseResult<String> toPuckUpPassenger(@RequestBody OrderRequest request) {
        return apiOrderService.toPuckUpPassenger(request);
    }

    /**
     * 到达乘客上车点
     */
    @PostMapping("/arrived-departure")
    public ResponseResult<String> arrivedDeparture(@RequestBody OrderRequest orderRequest) {
        return apiOrderService.arrivedDeparture(orderRequest);
    }

    /**
     * 司机接到乘客
     */
    @PostMapping("/puck-up-passenger")
    public ResponseResult<String> pucUpPassenger(@RequestBody OrderRequest request) {
        return apiOrderService.pucUpPassenger(request);
    }

    /**
     * 乘客到达目的地，下车，行程终止
     */
    @PostMapping("/passenger-getoff")
    public ResponseResult<String> passengerGetOff(@RequestBody OrderRequest orderRequest) {
        return apiOrderService.passengerGetOff(orderRequest);
    }
}
