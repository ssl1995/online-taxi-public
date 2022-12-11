package com.ssl.note.controller;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.ServiceOrderClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SongShengLin
 * @date 2022/11/18 22:05
 * @description
 */
@RestController
@Slf4j
public class TestController {

    @Autowired
    private ServiceOrderClient serviceOrderClient;
    @Value("${server.port}")
    private String port;

    @GetMapping("/autTest")
    public ResponseResult<String> authTest() {
        return ResponseResult.success("auth test");
    }


    @GetMapping("/no-autTest")
    public ResponseResult<String> noAuthTest() {
        return ResponseResult.success("no-autTest");
    }

    @GetMapping("/test/order/add/{orderId}")
    public String orderAddTerminalTest(@PathVariable("orderId") Long orderId) {
        log.info("api-passenger服务,端口:{},接收到orderId:{}", port, orderId);
        return serviceOrderClient.orderAddTerminalTest(orderId);
    }

}
