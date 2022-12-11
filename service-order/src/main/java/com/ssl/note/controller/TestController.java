package com.ssl.note.controller;

import com.ssl.note.dto.OrderInfo;
import com.ssl.note.mapper.OrderInfoMapper;
import com.ssl.note.remote.TerminalClient;
import com.ssl.note.service.OrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/09 14:59
 * @Describe: 测试方法的接口
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Autowired
    private TerminalClient terminalClient;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @PostMapping("/order/add/terminal")
    public String orderAddTerminalTest(@RequestBody OrderInfo OrderInfo) {
        orderInfoService.dispatchRealTimeOrder(OrderInfo);
        return "real-test-orderInfo-add success";
    }

    @GetMapping("/order/add/{orderId}")
    public String orderAddTerminalTest(@PathVariable("orderId") Long orderId) {
        log.info("orderId:{}", orderId);
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        orderInfoService.dispatchRealTimeOrder(orderInfo);
        return "real-test-order-add success";
    }
}
