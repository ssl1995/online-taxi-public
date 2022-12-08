package com.ssl.note.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.constant.OrderConstants;
import com.ssl.note.dto.OrderInfo;
import com.ssl.note.dto.PriceRule;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.mapper.OrderInfoMapper;
import com.ssl.note.remote.ServicePriceClient;
import com.ssl.note.request.OrderRequest;
import com.ssl.note.utils.RedisPrefixUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public ResponseResult<String> add(OrderRequest orderRequest) {
        // 检查:计价规则是否发生变化
        if (!isNewFareTypeAndVersion(orderRequest.getFareType(),orderRequest.getFareVersion())) {
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_CHANGED.getCode(), CommonStatusEnum.PRICE_RULE_CHANGED.getMessage());
        }

        // 检查:是否命中黑名单
        if (isBlackDevice(orderRequest.getDeviceCode())) {
            return ResponseResult.fail(CommonStatusEnum.DEVICE_IS_BLACK.getCode(), CommonStatusEnum.DEVICE_IS_BLACK.getMessage());
        }

        // 检查:下单的城市和计价规则是否正常
        if (!isPriceRuleExists(orderRequest.getFareType())) {
            return ResponseResult.fail(CommonStatusEnum.CITY_SERVICE_NOT_SERVICE.getCode(), CommonStatusEnum.CITY_SERVICE_NOT_SERVICE.getMessage());
        }

        // 检查:乘客是否有进行中的订单
        if ((isPassengerOrderGoingOn(orderRequest.getPassengerId())) > 0) {
            return ResponseResult.fail(CommonStatusEnum.ORDER_GOING_ON.getCode(), CommonStatusEnum.ORDER_GOING_ON.getMessage());
        }

        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(orderRequest, orderInfo);
        orderInfo.setGmtCreate(LocalDateTime.now());
        orderInfo.setGmtModified(LocalDateTime.now());
        orderInfoMapper.insert(orderInfo);

        return ResponseResult.success("");
    }

    private boolean isPriceRuleExists(String fareType) {
        int index = fareType.indexOf("$");
        String cityCode = fareType.substring(0, index);
        String vehicleType = fareType.substring(index);

        PriceRule priceRule = PriceRule.builder().cityCode(cityCode).vehicleType(vehicleType).build();
        ResponseResult<Boolean> isExists = servicePriceClient.isExists(priceRule);
        if (!Objects.equals(isExists.getCode(), CommonStatusEnum.SUCCESS.getCode()) || !isExists.getData()) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private boolean isBlackDevice(String deviceCode) {
        String key = RedisPrefixUtils.BLACK_DEVICE_CODE_PREFIX + deviceCode;
        Boolean isExist = stringRedisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(isExist)) {
            String countStr = stringRedisTemplate.opsForValue().get(key);
            assert countStr != null;
            int count = Integer.parseInt(countStr);
            // 次数>=2就是命中黑名单
            if (count >= 2) {
                return Boolean.TRUE;
            } else {
                stringRedisTemplate.opsForValue().increment(key);
            }
        }

        stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 1L, TimeUnit.HOURS);

        return Boolean.FALSE;
    }

    private int isPassengerOrderGoingOn(Long passengerId) {
        // 判断有正在进行的订单不允许下单
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("passenger_id", passengerId);
        // 订单状态：1-7都是无法下单
        queryWrapper.and(wrapper -> wrapper
                .eq("order_status", OrderConstants.ORDER_START)
                .or().eq("order_status", OrderConstants.DRIVER_RECEIVE_ORDER)
                .or().eq("order_status", OrderConstants.DRIVER_TO_PICK_UP_PASSENGER)
                .or().eq("order_status", OrderConstants.DRIVER_ARRIVED_DEPARTURE)
                .or().eq("order_status", OrderConstants.PICK_UP_PASSENGER)
                .or().eq("order_status", OrderConstants.PASSENGER_GETOFF)
                .or().eq("order_status", OrderConstants.TO_START_PAY)
        );

        return orderInfoMapper.selectCount(queryWrapper);
    }

    private boolean isNewFareTypeAndVersion(String fareType, Integer fareVersion) {
        ResponseResult<Boolean> isNewResp = servicePriceClient.isNew(fareType, fareVersion);
        if (!Objects.equals(isNewResp.getCode(), CommonStatusEnum.SUCCESS.getCode()) || !isNewResp.getData()) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
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
