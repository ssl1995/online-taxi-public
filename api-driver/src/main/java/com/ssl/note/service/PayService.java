package com.ssl.note.service;

import com.ssl.note.constant.IdentityConstant;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.ServiceSsePushClient;
import com.ssl.note.request.OrderRequest;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/20 16:06
 * @Describe:
 */
@Service
@Slf4j
public class PayService {

    @Autowired
    private ServiceSsePushClient ssePushClient;

    @Autowired
    private ApiOrderService orderService;

    public ResponseResult<String> pushPayInfo(String orderId, String passengerId, String price) {
        // 推送消息给乘客
        JSONObject message = new JSONObject();
        message.put("price", price);
        message.put("orderId", orderId);
        // 修改订单状态
        OrderRequest orderRequest = OrderRequest.builder().orderId(orderId).build();
        orderService.pushPayInfo(orderRequest);

        String pushMessage = ssePushClient.push(Long.parseLong(passengerId), IdentityConstant.PASSENGER_IDENTITY, message.toString());
        log.info(pushMessage);


        return ResponseResult.success();
    }
}
