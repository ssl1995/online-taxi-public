package com.ssl.note.controller;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/20 16:05
 * @Describe:
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private PayService payService;

    @PostMapping("/push-pay-info")
    public ResponseResult<String> pushPayInfo(@RequestParam("orderId") String orderId,
                                              @RequestParam("passengerId") String passengerId,
                                              @RequestParam("price") String price) {
        return payService.pushPayInfo(orderId, passengerId, price);
    }
}
