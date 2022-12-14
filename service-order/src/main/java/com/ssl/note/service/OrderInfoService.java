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
        // ????????????????????????????????????
        if (!isAvailableDriver(orderRequest.getAddress())) {
            return ResponseResult.fail(CommonStatusEnum.CITY_DRIVER_EMPTY.getCode(), CommonStatusEnum.CITY_DRIVER_EMPTY.getMessage());
        }

        // ??????:??????????????????????????????
        if (!isNewFareTypeAndVersion(orderRequest.getFareType(), orderRequest.getFareVersion())) {
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_CHANGED.getCode(), CommonStatusEnum.PRICE_RULE_CHANGED.getMessage());
        }

        // ??????:?????????????????????
        if (isBlackDevice(orderRequest.getDeviceCode())) {
            return ResponseResult.fail(CommonStatusEnum.DEVICE_IS_BLACK.getCode(), CommonStatusEnum.DEVICE_IS_BLACK.getMessage());
        }

        // ??????:??????????????????????????????????????????
        if (!isPriceRuleExists(orderRequest.getFareType())) {
            return ResponseResult.fail(CommonStatusEnum.CITY_SERVICE_NOT_SERVICE.getCode(), CommonStatusEnum.CITY_SERVICE_NOT_SERVICE.getMessage());
        }

        // ??????:?????????????????????????????????
        if ((isPassengerOrderGoingOn(orderRequest.getPassengerId())) > 0) {
            return ResponseResult.fail(CommonStatusEnum.ORDER_GOING_ON.getCode(), CommonStatusEnum.ORDER_GOING_ON.getMessage());
        }

        // ????????????
        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(orderRequest, orderInfo);
        orderInfo.setGmtCreate(LocalDateTime.now());
        orderInfo.setGmtModified(LocalDateTime.now());
        orderInfoMapper.insert(orderInfo);

        // ????????????????????????????????????????????????????????????20s
        for (int i = 1; i <= 6; i++) {
            int isSuccess = dispatchRealTimeOrder(orderInfo);

            // ??????????????????
            if (Objects.equals(isSuccess, CommonStatusEnum.SUCCESS.getCode())) {
                break;
            }

            // ?????????????????????????????????????????????????????????
            if (i == 6) {
                orderInfo.setOrderStatus(OrderConstants.ORDER_INVALID);
                orderInfoMapper.updateById(orderInfo);
                log.info("??????id={},?????????", orderInfo.getId());
                break;
            }

            // ??????????????????????????????20s
            try {
                Thread.sleep(20 * 1000);
            } catch (Exception e) {
                log.error("??????20s??????");
            }
        }

        return ResponseResult.success("");
    }


    /**
     * ????????????????????????????????????
     */
    public int dispatchRealTimeOrder(OrderInfo orderInfo) {
        // ????????????????????????,0=??????,1=??????
        int result = 0;

        // ???????????????
        String depLatitude = orderInfo.getDepLatitude();
        // ???????????????
        String depLongitude = orderInfo.getDepLongitude();

        // ????????????????????????
        String center = depLatitude + "," + depLongitude;
        String radius = null;
        List<String> radiusList = Lists.newArrayList("2000", "4000", "5000");
        ResponseResult<List<TerminalResponse>> terminalResp = null;

        // goto???????????????for????????????????????????????????????
        radius:
        for (int i = 0; i < radiusList.size(); i++) {
            radius = radiusList.get(i);
            // ??????????????????
            terminalResp = serviceMapClient.aroundSearch(center, radius);
            log.info("????????????" + radius + "???????????????????????????,?????????" + JSONArray.fromObject(terminalResp.getData()).toString());
            if (Objects.isNull(terminalResp.getData())) {
                continue;
            }
            // [{"carId":1601122702212026369,"latitude":"40.007473","longitude":"116.34769","tid":"609120858"}]
            List<TerminalResponse> terminalResponses = terminalResp.getData();

            for (TerminalResponse terminalResponse : terminalResponses) {
                Long carId = terminalResponse.getCarId();
                String terminalLongitude = terminalResponse.getLongitude();
                String TerminalLatitude = terminalResponse.getLatitude();

                // ??????????????????????????????
                ResponseResult<OrderDriverResponse> orderDriverResp = driverUserClient.getAvailableDriverByCarId(carId);
                if (Objects.equals(orderDriverResp.getCode(), CommonStatusEnum.AVAILABLE_DRIVER_EMPTY.getCode())) {
                    log.info("????????????ID???" + carId + ",???????????????");
                    continue;
                }
                OrderDriverResponse orderDriver = orderDriverResp.getData();
                log.info("orderDriver:{}", orderDriver);
                Long driverId = orderDriver.getDriverId();

                // ??????redisson????????????
                String lockKey = String.valueOf(driverId).intern();
                RLock lock = redissonClient.getLock(lockKey);

                try {
                    // ??????
                    lock.lock();

                    // ????????????????????????????????????????????????????????????
                    if (isDriverOrderGoingOn(driverId)) {
                        log.info("??????Id:{},??????Id:{},???????????????????????????????????????", carId, driverId);
                        continue;
                    }

                    String driverPhone = orderDriver.getDriverPhone();
                    String vehicleNo = orderDriver.getVehicleNo();
                    String licenseId = orderDriver.getLicenseId();
                    String vehicleTypeByCar = orderDriver.getVehicleType();

                    // ?????????????????????????????????CarId????????????
                    if (!orderInfo.getVehicleType().trim().equals(vehicleTypeByCar.trim())) {
                        log.info("???????????????????????????:{},?????????carId:???{}????????????????????????:{}????????????", orderInfo.getVehicleType(), carId, vehicleTypeByCar);
                        continue;
                    }

                    // ??????????????????
                    // ?????????????????????????????????????????????
                    orderInfo.setCarId(carId);
                    orderInfo.setDriverPhone(driverPhone);
                    orderInfo.setDriverId(driverId);
                    orderInfo.setLicenseId(licenseId);
                    orderInfo.setVehicleNo(vehicleNo);
                    // ????????????????????????
                    orderInfo.setReceiveOrderCarLongitude(terminalLongitude);
                    orderInfo.setReceiveOrderCarLatitude(TerminalLatitude);

                    orderInfo.setReceiveOrderTime(LocalDateTime.now());
                    orderInfo.setOrderStatus(OrderConstants.DRIVER_RECEIVE_ORDER);

                    orderInfoMapper.updateById(orderInfo);

                    // ?????????????????????
                    sendMsgToDriver(orderInfo, driverId);

                    // ?????????????????????
                    sendMsgToPassenger(orderInfo, carId);

                    // ?????????1?????????????????????????????????????????????
                    result = 1;
                    break radius;
                } finally {
                    // ???????????????????????????????????????????????????
                    if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                        // ??????????????????finally???
                        lock.unlock();
                    }
                }
            }
        }

        return result;
    }

    private void sendMsgToPassenger(OrderInfo orderInfo, Long carId) {
        JSONObject passengerContent = new JSONObject();
        // ????????????????????????
        passengerContent.put("orderId", orderInfo.getId());
        passengerContent.put("driverId", orderInfo.getDriverId());
        passengerContent.put("driverPhone", orderInfo.getDriverPhone());
        passengerContent.put("vehicleNo", orderInfo.getVehicleNo());

        // ?????????????????????????????????????????????
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
        // ?????????????????????????????????
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
        // ?????????????????????2-5??????????????????????????????
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
            log.error("isAvailableDriver?????????");
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
            // ??????>=2?????????????????????
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
        // ?????????????????????????????????????????????
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("passenger_id", passengerId);
        // ???????????????1-7??????????????????
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
            return ResponseResult.fail(CommonStatusEnum.FAIL.getCode(), "id????????????");
        }
        OrderInfo orderInfo = orderInfoMapper.selectById(id);
        if (Objects.isNull(orderInfo)) {
            return ResponseResult.fail(CommonStatusEnum.FAIL.getCode(), "??????????????????");
        }
        return ResponseResult.success(orderInfo);
    }

    public ResponseResult<String> toPuckUpPassenger(OrderRequest orderRequest) {
        String orderId = orderRequest.getOrderId();
        // ?????????????????????????????????????????????
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

        // ???????????????????????????????????????
        ResponseResult<Car> carResponse = driverUserClient.getCarById(orderInfo.getCarId());
        if (!Objects.equals(carResponse.getCode(), CommonStatusEnum.SUCCESS.getCode())) {
            return ResponseResult.fail(carResponse.getCode(), carResponse.getMessage());
        }

        String tid = carResponse.getData().getTid();
        // localDateTime??????????????????????????????
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

        // ??????????????????
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

        // ????????????
        int cancelType = 1;

        // ?????????????????????
        if (identity.trim().equals(IdentityConstant.PASSENGER_IDENTITY)) {
            switch (orderStatus) {
                // ????????????
                case OrderConstants.ORDER_START:
                    cancelTypeCode = OrderConstants.CANCEL_PASSENGER_BEFORE;
                    break;
                // ??????????????????
                case OrderConstants.DRIVER_RECEIVE_ORDER:
                    LocalDateTime receiveOrderTime = orderInfo.getReceiveOrderTime();
                    // ???????????????????????????
                    long between = ChronoUnit.MINUTES.between(receiveOrderTime, cancelTime);
                    if (between > 1) {
                        cancelTypeCode = OrderConstants.CANCEL_PASSENGER_ILLEGAL;
                    } else {
                        cancelTypeCode = OrderConstants.CANCEL_PASSENGER_BEFORE;
                    }
                    break;
                // ??????????????????
                case OrderConstants.DRIVER_TO_PICK_UP_PASSENGER:
                    // ????????????????????????
                case OrderConstants.DRIVER_ARRIVED_DEPARTURE:
                    cancelTypeCode = OrderConstants.CANCEL_PASSENGER_ILLEGAL;
                    break;
                default:
                    log.info("??????????????????");
                    cancelType = 0;
                    break;
            }
        }

        // ?????????????????????
        if (identity.trim().equals(IdentityConstant.DIVER_IDENTITY)) {
            switch (orderStatus) {
                // ????????????
                // ??????????????????
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
                    log.info("??????????????????");
                    cancelType = 0;
                    break;
            }
        }

        // ???????????????????????????
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
