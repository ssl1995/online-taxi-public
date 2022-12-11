package com.ssl.note.controller;

import com.ssl.note.dto.OrderInfo;
import com.ssl.note.remote.TerminalClient;
import com.ssl.note.service.OrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/09 14:59
 * @Describe: 测试方法的接口
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TerminalClient terminalClient;

    @Autowired
    private OrderInfoService orderInfoService;

    @PostMapping("/order/add/terminal")
    public int orderAddTerminalTest(@RequestBody OrderInfo OrderInfo) {
        return orderInfoService.dispatchRealTimeOrder(OrderInfo);
    }
}
