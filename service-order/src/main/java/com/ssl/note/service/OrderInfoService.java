package com.ssl.note.service;

import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.dto.OrderInfo;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.mapper.OrderInfoMapper;
import com.ssl.note.remote.ServicePriceClient;
import com.ssl.note.request.OrderRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/06 21:45
 * @Describe:
 */
@Service
public class OrderInfoService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private ServicePriceClient servicePriceClient;

    public ResponseResult<String> add(OrderRequest orderRequest) {
        // 1.检查计价规则是否发生变化
        ResponseResult<Boolean> isNewResp = servicePriceClient.isNew(orderRequest.getFareType(), orderRequest.getFareVersion());
        if (!Objects.equals(isNewResp.getCode(), CommonStatusEnum.SUCCESS.getCode()) || !isNewResp.getData()) {
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_CHANGED.getCode(), CommonStatusEnum.PRICE_RULE_CHANGED.getMessage());
        }

        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(orderRequest, orderInfo);
        orderInfo.setGmtCreate(LocalDateTime.now());
        orderInfo.setGmtModified(LocalDateTime.now());
        orderInfoMapper.insert(orderInfo);

        return ResponseResult.success("");
    }

    public ResponseResult<OrderInfo> getById(Long id) {
        if (Objects.isNull(id)) {
            return ResponseResult.fail(CommonStatusEnum.FAIL.getCode(), "id不能为空");
        }
        OrderInfo orderInfo = orderInfoMapper.selectById(id);
        if (Objects.isNull(orderInfo)) {
            return ResponseResult.fail(CommonStatusEnum.FAIL.getCode(), "该数据不存在");
        }
        return ResponseResult.success(orderInfo);
    }
}
