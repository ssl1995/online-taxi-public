package com.ssl.note.service;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.ServiceOrderClient;
import com.ssl.note.request.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/19 21:41
 * @Describe:
 */
@Service
public class ApiOrderService {
    @Autowired
    private ServiceOrderClient serviceOrderClient;

    /**
     * 去接乘客
     */
    public ResponseResult<String> toPuckUpPassenger(OrderRequest request) {
        return serviceOrderClient.toPuckUpPassenger(request);
    }

    /**
     * 到达乘客上车点
     */
    public ResponseResult<String> arrivedDeparture(OrderRequest orderRequest) {
        return serviceOrderClient.arrivedDeparture(orderRequest);

    }

    /**
     * 司机接到乘客
     */
    public ResponseResult<String> pucUpPassenger(OrderRequest request) {
        return serviceOrderClient.pucUpPassenger(request);
    }

    /**
     * 乘客到达目的地，下车，行程终止
     */
    public ResponseResult<String> passengerGetOff(OrderRequest orderRequest) {
        return serviceOrderClient.passengerGetOff(orderRequest);
    }

    /**
     * 乘客到达目的地，下车，行程终止
     */
    public ResponseResult<String> cancel(String orderId, String identity) {
        return serviceOrderClient.cancel(orderId, identity);
    }
}
