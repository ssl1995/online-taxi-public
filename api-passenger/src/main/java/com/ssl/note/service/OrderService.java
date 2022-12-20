package com.ssl.note.service;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.ServiceOrderClient;
import com.ssl.note.request.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/05 16:28
 * @Describe:
 */
@Service
public class OrderService {

    @Autowired
    private ServiceOrderClient serviceOrderClient;

    public ResponseResult<String> add(@RequestBody OrderRequest orderRequest) {
        return serviceOrderClient.add(orderRequest);
    }

    /**
     * 乘客到达目的地，下车，行程终止
     */
    public ResponseResult<String> cancel(String orderId, String identity) {
        return serviceOrderClient.cancel(orderId, identity);
    }
}
