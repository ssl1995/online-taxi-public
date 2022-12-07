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

    @PostMapping("/add")
    public ResponseResult<String> add(@RequestBody OrderRequest orderRequest) {
        log.info("service-order接收到请求参数:{}", orderRequest);
        return orderInfoService.add(orderRequest);
    }

    @GetMapping("/{id}")
    public ResponseResult<OrderInfo> getById(@PathVariable("id") Long id) {
        return orderInfoService.getById(id);
    }
}
