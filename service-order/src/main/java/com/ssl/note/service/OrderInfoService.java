package com.ssl.note.service;

import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.dto.OrderInfo;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.mapper.OrderInfoMapper;
import com.ssl.note.request.ApiDriverPointRequest;
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

    public ResponseResult<String> add(OrderRequest orderRequest) {
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
