package com.ssl.note.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.google.common.collect.Lists;
import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.constant.IdentityConstant;
import com.ssl.note.constant.OrderConstants;
import com.ssl.note.dto.Car;
import com.ssl.note.dto.OrderInfo;
import com.ssl.note.dto.PriceRule;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.mapper.OrderInfoMapper;
import com.ssl.note.remote.DriverUserClient;
import com.ssl.note.remote.ServiceMapClient;
import com.ssl.note.remote.ServicePriceClient;
import com.ssl.note.remote.SsePushClient;
import com.ssl.note.request.OrderRequest;
import com.ssl.note.request.PriceRuleIsNewRequest;
import com.ssl.note.response.OrderDriverResponse;
import com.ssl.note.response.TerminalResponse;
import com.ssl.note.response.TrSearchResponse;
import com.ssl.note.utils.RedisPrefixUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/06 21:45
 * @Describe:
 */
@Service
@Slf4j
public class OrderInfoService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private ServicePriceClient servicePriceClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DriverUserClient driverUserClient;

    @Autowired
    private ServiceMapClient serviceMapClient;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private SsePushClient ssePushClient;

    public ResponseResult<String> add(OrderRequest orderRequest) {
        // 检查：当前城市是否有司机
        if (!isAvailableDriver(orderRequest.getAddress())) {
            return ResponseResult.fail(CommonStatusEnum.CITY_DRIVER_EMPTY.getCode(), CommonStatusEnum.CITY_DRIVER_EMPTY.getMessage());
        }

        // 检查:计价规则是否发生变化
        if (!isNewFareTypeAndVersion(orderRequest.getFareType(), orderRequest.getFareVersion())) {
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

        // 保存订单
        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(orderRequest, orderInfo);
        orderInfo.setGmtCreate(LocalDateTime.now());
        orderInfo.setGmtModified(LocalDateTime.now());
        orderInfoMapper.insert(orderInfo);

        // 循环分配订单，并更新订单，每一次分配暂停20s
        for (int i = 1; i <= 6; i++) {
            int isSuccess = dispatchRealTimeOrder(orderInfo);

            // 分配司机成功
            if (Objects.equals(isSuccess, CommonStatusEnum.SUCCESS.getCode())) {
                break;
            }

            // 循环来到最后一次没有找到司机，订单无效
            if (i == 6) {
                orderInfo.setOrderStatus(OrderConstants.ORDER_INVALID);
                orderInfoMapper.updateById(orderInfo);
                log.info("订单id={},无效！", orderInfo.getId());
                break;
            }

            // 下次寻找司机前，等待20s
            try {
                Thread.sleep(20 * 1000);
            } catch (Exception e) {
                log.error("暂停20s失败");
            }
        }

        return ResponseResult.success("");
    }


    /**
     * 保存订单成功后，分配订单
     */
    public int dispatchRealTimeOrder(OrderInfo orderInfo) {
        // 标识循环是否继续,0=失败,1=成功
        int result = 0;

        // 目的地纬度
        String depLatitude = orderInfo.getDepLatitude();
        // 目的地经度
        String depLongitude = orderInfo.getDepLongitude();

        // 获取附近终端信息
        String center = depLatitude + "," + depLongitude;
        String radius = null;
        List<String> radiusList = Lists.newArrayList("2000", "4000", "5000");
        ResponseResult<List<TerminalResponse>> terminalResp = null;

        // goto语法，内层for循环结束，外层也直接跳出
        radius:
        for (int i = 0; i < radiusList.size(); i++) {
            radius = radiusList.get(i);
            // 搜索周边终端
            terminalResp = serviceMapClient.aroundSearch(center, radius);
            log.info("在半径为" + radius + "的范围内，寻找车辆,结果：" + JSONArray.fromObject(terminalResp.getData()).toString());
            if (Objects.isNull(terminalResp.getData())) {
                continue;
            }
            // [{"carId":1601122702212026369,"latitude":"40.007473","longitude":"116.34769","tid":"609120858"}]
            List<TerminalResponse> terminalResponses = terminalResp.getData();

            for (TerminalResponse terminalResponse : terminalResponses) {
                Long carId = terminalResponse.getCarId();
                String terminalLongitude = terminalResponse.getLongitude();
                String TerminalLatitude = terminalResponse.getLatitude();

                // 获取有效司机车辆信息
                ResponseResult<OrderDriverResponse> orderDriverResp = driverUserClient.getAvailableDriverByCarId(carId);
                if (Objects.equals(orderDriverResp.getCode(), CommonStatusEnum.AVAILABLE_DRIVER_EMPTY.getCode())) {
                    log.info("没有车辆ID：" + carId + ",对于的司机");
                    continue;
                }
                OrderDriverResponse orderDriver = orderDriverResp.getData();
                log.info("orderDriver:{}", orderDriver);
                Long driverId = orderDriver.getDriverId();

                // 使用redisson分布式锁
                String lockKey = String.valueOf(driverId).intern();
                RLock lock = redissonClient.getLock(lockKey);

                try {
                    // 加锁
                    lock.lock();

                    // 检查：订单表中该司机是否还有进行中的订单
                    if (isDriverOrderGoingOn(driverId)) {
                        log.info("车辆Id:{},司机Id:{},上一单还在继续中，无法接单", carId, driverId);
                        continue;
                    }

                    String driverPhone = orderDriver.getDriverPhone();
                    String vehicleNo = orderDriver.getVehicleNo();
                    String licenseId = orderDriver.getLicenseId();
                    String vehicleTypeByCar = orderDriver.getVehicleType();

                    // 判断订单车型与终端绑定CarId是否匹配
                    if (!orderInfo.getVehicleType().trim().equals(vehicleTypeByCar.trim())) {
                        log.info("当前订单的车辆类型:{},与终端carId:【{}】绑定的车辆类型:{}不匹配！", orderInfo.getVehicleType(), carId, vehicleTypeByCar);
                        continue;
                    }

                    // 更新订单信息
                    // 设置订单中和司机车辆相关的信息
                    orderInfo.setCarId(carId);
                    orderInfo.setDriverPhone(driverPhone);
                    orderInfo.setDriverId(driverId);
                    orderInfo.setLicenseId(licenseId);
                    orderInfo.setVehicleNo(vehicleNo);
                    // 从地图中终端获取
                    orderInfo.setReceiveOrderCarLongitude(terminalLongitude);
                    orderInfo.setReceiveOrderCarLatitude(TerminalLatitude);

                    orderInfo.setReceiveOrderTime(LocalDateTime.now());
                    orderInfo.setOrderStatus(OrderConstants.DRIVER_RECEIVE_ORDER);

                    orderInfoMapper.updateById(orderInfo);

                    // 发送消息给司机
                    sendMsgToDriver(orderInfo, driverId);

                    // 发送消息给乘客
                    sendMsgToPassenger(orderInfo, carId);

                    // 一旦有1个司机接单，外层循环也一起结束
                    result = 1;
                    break radius;
                } finally {
                    // 问题：避免解锁太快，把别人给解锁了
                    if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                        // 解锁统一放在finally里
                        lock.unlock();
                    }
                }
            }
        }

        return result;
    }

    private void sendMsgToPassenger(OrderInfo orderInfo, Long carId) {
        JSONObject passengerContent = new JSONObject();
        // 乘客需要司机信息
        passengerContent.put("orderId", orderInfo.getId());
        passengerContent.put("driverId", orderInfo.getDriverId());
        passengerContent.put("driverPhone", orderInfo.getDriverPhone());
        passengerContent.put("vehicleNo", orderInfo.getVehicleNo());

        // 乘客需要车辆信息，调用车辆服务
        ResponseResult<Car> carById = driverUserClient.getCarById(carId);
        Car carRemote = carById.getData();
        passengerContent.put("brand", carRemote.getBrand());
        passengerContent.put("model", carRemote.getModel());
        passengerContent.put("vehicleColor", carRemote.getVehicleColor());

        passengerContent.put("receiveOrderCarLongitude", orderInfo.getReceiveOrderCarLongitude());
        passengerContent.put("receiveOrderCarLatitude", orderInfo.getReceiveOrderCarLatitude());

        ssePushClient.push(orderInfo.getPassengerId(), IdentityConstant.PASSENGER_IDENTITY, passengerContent.toString());
    }

    private void sendMsgToDriver(OrderInfo orderInfo, Long driverId) {
        JSONObject driverContent = new JSONObject();
        // 司机需要知道乘客的信息
        driverContent.put("orderId", orderInfo.getId());
        driverContent.put("passengerId", orderInfo.getPassengerId());
        driverContent.put("passengerPhone", orderInfo.getPassengerPhone());
        driverContent.put("departure", orderInfo.getDeparture());
        driverContent.put("depLongitude", orderInfo.getDepLongitude());
        driverContent.put("depLatitude", orderInfo.getDepLatitude());

        driverContent.put("destination", orderInfo.getDestination());
        driverContent.put("destLongitude", orderInfo.getDestLongitude());
        driverContent.put("destLatitude", orderInfo.getDestLatitude());

        ssePushClient.push(driverId, IdentityConstant.DIVER_IDENTITY, driverContent.toString());
    }


    private boolean isDriverOrderGoingOn(Long driverId) {
        // 司机接单状态为2-5表示上一单正在继续中
        return new LambdaQueryChainWrapper<>(orderInfoMapper)
                .eq(OrderInfo::getDriverId, driverId)
                .and(wrapper -> wrapper
                        .eq(OrderInfo::getOrderStatus, OrderConstants.DRIVER_RECEIVE_ORDER)
                        .or().eq(OrderInfo::getOrderStatus, OrderConstants.DRIVER_ARRIVED_DEPARTURE)
                        .or().eq(OrderInfo::getOrderStatus, OrderConstants.DRIVER_ARRIVED_DEPARTURE)
                        .or().eq(OrderInfo::getOrderStatus, OrderConstants.PICK_UP_PASSENGER)
                ).count() > 0;
    }

    private boolean isAvailableDriver(String cityCode) {
        ResponseResult<Boolean> isAvailableDriver = driverUserClient.isAvailableDriver(cityCode);
        if (Objects.isNull(isAvailableDriver) || !Objects.equals(isAvailableDriver.getCode(), CommonStatusEnum.SUCCESS.getCode())) {
            log.error("isAvailableDriver为空！");
            return Boolean.FALSE;
        }
        return isAvailableDriver.getData();
    }


    private boolean isPriceRuleExists(String fareType) {
        int index = fareType.indexOf("$");
        String cityCode = fareType.substring(0, index);
        String vehicleType = fareType.substring(index + 1);

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

    private int isPassengerOrderGoingOn(String passengerId) {
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
        PriceRuleIsNewRequest priceRuleIsNewRequest = new PriceRuleIsNewRequest();
        priceRuleIsNewRequest.setFareType(fareType);
        priceRuleIsNewRequest.setFareVersion(fareVersion);
        ResponseResult<Boolean> isNewResp = servicePriceClient.isNew(priceRuleIsNewRequest);
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

    public ResponseResult<String> toPuckUpPassenger(OrderRequest orderRequest) {
        String orderId = orderRequest.getOrderId();
        // 接乘客时间不用传，后台自己生成
//        LocalDateTime toPickUpPassengerTime = orderRequest.getToPickUpPassengerTime();
        String toPickUpPassengerLongitude = orderRequest.getToPickUpPassengerLongitude();
        String toPickUpPassengerLatitude = orderRequest.getToPickUpPassengerLatitude();
        String toPickUpPassengerAddress = orderRequest.getToPickUpPassengerAddress();

        OrderInfo orderInfo = new LambdaQueryChainWrapper<>(orderInfoMapper)
                .eq(OrderInfo::getId, orderId).one();

        orderInfo.setToPickUpPassengerAddress(toPickUpPassengerAddress);
        orderInfo.setToPickUpPassengerLatitude(toPickUpPassengerLatitude);
        orderInfo.setToPickUpPassengerLongitude(toPickUpPassengerLongitude);
        orderInfo.setToPickUpPassengerTime(LocalDateTime.now());
        orderInfo.setOrderStatus(OrderConstants.DRIVER_TO_PICK_UP_PASSENGER);

        orderInfoMapper.updateById(orderInfo);

        return ResponseResult.success();
    }

    public ResponseResult<String> arrivedDeparture(OrderRequest orderRequest) {
        String orderId = orderRequest.getOrderId();
        OrderInfo orderInfo = new LambdaQueryChainWrapper<>(orderInfoMapper)
                .eq(OrderInfo::getId, orderId).one();

        orderInfo.setDriverArrivedDepartureTime(LocalDateTime.now());
        orderInfo.setOrderStatus(OrderConstants.DRIVER_ARRIVED_DEPARTURE);

        orderInfoMapper.updateById(orderInfo);

        return ResponseResult.success();
    }

    public ResponseResult<String> puckUpPassenger(OrderRequest orderRequest) {
        String orderId = orderRequest.getOrderId();
        OrderInfo orderInfo = new LambdaQueryChainWrapper<>(orderInfoMapper)
                .eq(OrderInfo::getId, orderId).one();

        orderInfo.setPickUpPassengerLongitude(orderRequest.getPickUpPassengerLongitude());
        orderInfo.setPickUpPassengerLatitude(orderRequest.getPickUpPassengerLatitude());
        orderInfo.setPickUpPassengerTime(LocalDateTime.now());
        orderInfo.setOrderStatus(OrderConstants.PICK_UP_PASSENGER);

        orderInfoMapper.updateById(orderInfo);
        return ResponseResult.success();
    }

    public ResponseResult<String> passengerGetOff(OrderRequest orderRequest) {
        String orderId = orderRequest.getOrderId();
        OrderInfo orderInfo = new LambdaQueryChainWrapper<>(orderInfoMapper)
                .eq(OrderInfo::getId, orderId).one();

        orderInfo.setPassengerGetoffTime(LocalDateTime.now());
        orderInfo.setPassengerGetoffLongitude(orderRequest.getPassengerGetoffLongitude());
        orderInfo.setPassengerGetoffLatitude(orderRequest.getPassengerGetoffLatitude());
        orderInfo.setOrderStatus(OrderConstants.PASSENGER_GETOFF);

        // 获取总的行程距离和行程时长
        ResponseResult<Car> carResponse = driverUserClient.getCarById(orderInfo.getCarId());
        if (!Objects.equals(carResponse.getCode(), CommonStatusEnum.SUCCESS.getCode())) {
            return ResponseResult.fail(carResponse.getCode(), carResponse.getMessage());
        }

        String tid = carResponse.getData().getTid();
        // localDateTime设置时区和获取毫秒数
        Long startTime = orderInfo.getPickUpPassengerTime().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        Long endTime = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();

        ResponseResult<TrSearchResponse> trSearchResponse = serviceMapClient.trSearch(tid, startTime, endTime);
        if (!Objects.equals(trSearchResponse.getCode(), CommonStatusEnum.SUCCESS.getCode())) {
            return ResponseResult.fail(trSearchResponse.getCode(), trSearchResponse.getMessage());
        }

        TrSearchResponse trSearch = trSearchResponse.getData();
        Long driveMile = trSearch.getDriveMile();
        Long driveTime = trSearch.getDriveTime();
        orderInfo.setDriveMile(driveMile);
        orderInfo.setDriveTime(driveTime);

        // 计算行程价格
        ResponseResult<Double> priceResponse = servicePriceClient.calculatePrice(driveMile.intValue(), driveTime.intValue(), orderInfo.getAddress(), orderInfo.getVehicleType());
        if (!Objects.equals(priceResponse.getCode(), CommonStatusEnum.SUCCESS.getCode())) {
            return ResponseResult.fail(priceResponse.getCode(), priceResponse.getMessage());
        }
        orderInfo.setPrice(priceResponse.getData());

        orderInfoMapper.updateById(orderInfo);
        return ResponseResult.success();
    }

    public ResponseResult<String> pay(OrderRequest orderRequest) {
        String orderId = orderRequest.getOrderId();
        OrderInfo orderInfo = new LambdaQueryChainWrapper<>(orderInfoMapper)
                .eq(OrderInfo::getId, orderId).one();
        orderInfo.setOrderStatus(OrderConstants.SUCCESS_PAY);

        orderInfoMapper.updateById(orderInfo);
        return ResponseResult.success();
    }

    public ResponseResult<String> cancel(String orderId, String identity) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        Integer orderStatus = orderInfo.getOrderStatus();

        LocalDateTime cancelTime = LocalDateTime.now();
        Integer cancelTypeCode = null;

        // 正常取消
        int cancelType = 1;

        // 如果是乘客取消
        if (identity.trim().equals(IdentityConstant.PASSENGER_IDENTITY)) {
            switch (orderStatus) {
                // 订单开始
                case OrderConstants.ORDER_START:
                    cancelTypeCode = OrderConstants.CANCEL_PASSENGER_BEFORE;
                    break;
                // 司机接到订单
                case OrderConstants.DRIVER_RECEIVE_ORDER:
                    LocalDateTime receiveOrderTime = orderInfo.getReceiveOrderTime();
                    // 计算两个时间的差值
                    long between = ChronoUnit.MINUTES.between(receiveOrderTime, cancelTime);
                    if (between > 1) {
                        cancelTypeCode = OrderConstants.CANCEL_PASSENGER_ILLEGAL;
                    } else {
                        cancelTypeCode = OrderConstants.CANCEL_PASSENGER_BEFORE;
                    }
                    break;
                // 司机去接乘客
                case OrderConstants.DRIVER_TO_PICK_UP_PASSENGER:
                    // 司机到达乘客起点
                case OrderConstants.DRIVER_ARRIVED_DEPARTURE:
                    cancelTypeCode = OrderConstants.CANCEL_PASSENGER_ILLEGAL;
                    break;
                default:
                    log.info("乘客取消失败");
                    cancelType = 0;
                    break;
            }
        }

        // 如果是司机取消
        if (identity.trim().equals(IdentityConstant.DIVER_IDENTITY)) {
            switch (orderStatus) {
                // 订单开始
                // 司机接到乘客
                case OrderConstants.DRIVER_RECEIVE_ORDER:
                case OrderConstants.DRIVER_TO_PICK_UP_PASSENGER:
                case OrderConstants.DRIVER_ARRIVED_DEPARTURE:
                    LocalDateTime receiveOrderTime = orderInfo.getReceiveOrderTime();
                    long between = ChronoUnit.MINUTES.between(receiveOrderTime, cancelTime);
                    if (between > 1) {
                        cancelTypeCode = OrderConstants.CANCEL_DRIVER_ILLEGAL;
                    } else {
                        cancelTypeCode = OrderConstants.CANCEL_DRIVER_BEFORE;
                    }
                    break;
                default:
                    log.info("司机取消失败");
                    cancelType = 0;
                    break;
            }
        }

        // 判断是否是正常取消
        if (cancelType == 0) {
            return ResponseResult.fail(CommonStatusEnum.ORDER_CANCEL_ERROR.getCode(), CommonStatusEnum.ORDER_CANCEL_ERROR.getMessage());
        }

        orderInfo.setCancelTypeCode(cancelTypeCode);
        orderInfo.setCancelTime(cancelTime);
        orderInfo.setCancelOperator(Integer.parseInt(identity));
        orderInfo.setOrderStatus(OrderConstants.ORDER_CANCEL);

        orderInfoMapper.updateById(orderInfo);
        return ResponseResult.success();
    }

    public ResponseResult<String> pushPayInfo(OrderRequest orderRequest) {
        String orderId = orderRequest.getOrderId();
        OrderInfo orderInfo = new LambdaQueryChainWrapper<>(orderInfoMapper).eq(OrderInfo::getId, orderId).one();
        orderInfo.setOrderStatus(OrderConstants.TO_START_PAY);

        orderInfoMapper.updateById(orderInfo);
        return ResponseResult.success();
    }
}
