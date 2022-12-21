package com.ssl.note.service;

import com.ssl.note.remote.ServiceOrderClient;
import com.ssl.note.request.OrderRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AlipayService {

    @Resource
    private ServiceOrderClient serviceOrderClient;

    public void pay(String orderId) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderId(orderId);
        serviceOrderClient.pay(orderRequest);
    }
}
