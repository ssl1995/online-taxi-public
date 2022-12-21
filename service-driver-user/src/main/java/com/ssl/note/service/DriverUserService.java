package com.ssl.note.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.constant.DriverCarConstants;
import com.ssl.note.dto.*;
import com.ssl.note.mapper.CarMapper;
import com.ssl.note.mapper.DriverCarBindingRelationshipMapper;
import com.ssl.note.mapper.DriverUserMapper;
import com.ssl.note.mapper.DriverUserWorkStatusMapper;
import com.ssl.note.response.OrderDriverResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/25 21:22
 * @Describe:
 */
@Service
public class DriverUserService {

    @Autowired
    private DriverUserMapper driverUserMapper;

    @Autowired
    private DriverUserWorkStatusMapper driverUserWorkStatusMapper;

    @Autowired
    private DriverCarBindingRelationshipMapper driverCarBindingRelationshipMapper;

    @Autowired
    private CarMapper carMapper;

    public ResponseResult<DriverUser> getDriverUserByPhone(String driverPhone) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("driver_phone", driverPhone);
        map.put("state", DriverCarConstants.DRIVER_STATE_VALID);
        List<DriverUser> driverUsers = driverUserMapper.selectByMap(map);
        if (CollectionUtils.isEmpty(driverUsers)) {
            return ResponseResult.fail(CommonStatusEnum.DRIVER_NOT_EXIST.getCode(), CommonStatusEnum.DRIVER_NOT_EXIST.getMessage());
        }

        return ResponseResult.success(driverUsers.get(0));
    }

    public ResponseResult<String> addUser(DriverUser driverUser) {
        LocalDateTime now = LocalDateTime.now();
        driverUser.setGmtCreate(now);
        driverUser.setGmtModified(now);
        // 保存司机
        driverUserMapper.insert(driverUser);

        // 保存司机工作状态表
        DriverUserWorkStatus driverUserWorkStatus = new DriverUserWorkStatus();
        driverUserWorkStatus.setDriverId(driverUser.getId());
        driverUserWorkStatus.setWorkStatus(DriverCarConstants.DRIVER_WORK_STATUS_STOP);
        driverUserWorkStatus.setGmtCreate(now);
        driverUserWorkStatus.setGmtModified(now);
        driverUserWorkStatusMapper.insert(driverUserWorkStatus);

        return ResponseResult.success("");
    }

    public ResponseResult<String> updateUser(DriverUser driverUser) {
        LocalDateTime now = LocalDateTime.now();
        driverUser.setGmtModified(now);
        driverUserMapper.updateById(driverUser);
        return ResponseResult.success("");
    }

    public ResponseResult<OrderDriverResponse> getAvailableDriverByCarId(Long carId) {
        if (Objects.isNull(carId)) {
            return ResponseResult.fail(CommonStatusEnum.PARAM_EMPTY_ERROR);
        }
        // 1.根据carId查询driver_id
        DriverCarBindingRelationship bindingRelationship = getDriverCarBindingRelationship(carId);
        if (Objects.isNull(bindingRelationship)) {
            // 失败：司机和车辆绑定关系不存在
            return ResponseResult.fail(CommonStatusEnum.DRIVER_CAR_BIND_NOT_EXISTS);
        }

        // 2.根据driverId检查司机是否出车状态
        Long driverId = bindingRelationship.getDriverId();
        DriverUserWorkStatus workStatus = getDriverUserWorkStatus(driverId);
        if (Objects.isNull(workStatus)) {
            // 失败：可用的司机为空
            return ResponseResult.fail(CommonStatusEnum.AVAILABLE_DRIVER_EMPTY);
        }

        // 3.查询司机车辆
        DriverUser driverUser = getDriverUser(driverId);
        Car car = getCarById(carId);

        // 4.封装返回值
        OrderDriverResponse data = OrderDriverResponse.builder()
                .driverId(driverId)
                .driverPhone(driverUser.getDriverPhone())
                .carId(carId)
                .licenseId(driverUser.getLicenseId())
                .vehicleNo(car.getVehicleNo())
                .vehicleType(car.getVehicleType()).build();

        return ResponseResult.success(data);
    }

    public ResponseResult<DriverCarBindingRelationship> getDriverCarBindingRelationship(String driverPhone) {
        DriverUser driverUser = new LambdaQueryChainWrapper<>(driverUserMapper)
                .eq(DriverUser::getDriverPhone, driverPhone).one();

        DriverCarBindingRelationship bindingRelationship = new LambdaQueryChainWrapper<>(driverCarBindingRelationshipMapper)
                .eq(DriverCarBindingRelationship::getDriverId, driverUser.getId())
                // 出车的状态
                .eq(DriverCarBindingRelationship::getBindState, DriverCarConstants.DRIVER_WORK_STATUS_START).one();

        return ResponseResult.success(bindingRelationship);
    }

    private Car getCarById(Long carId) {
        return new LambdaQueryChainWrapper<>(carMapper)
                .eq(Car::getId, carId)
                .one();
    }

    private DriverUser getDriverUser(Long driverId) {
        return new LambdaQueryChainWrapper<>(driverUserMapper)
                .eq(DriverUser::getId, driverId)
                .one();
    }

    private DriverUserWorkStatus getDriverUserWorkStatus(Long driverId) {
        return new LambdaQueryChainWrapper<>(driverUserWorkStatusMapper)
                .eq(DriverUserWorkStatus::getDriverId, driverId)
                .eq(DriverUserWorkStatus::getWorkStatus, DriverCarConstants.DRIVER_WORK_STATUS_START)
                .one();
    }

    private DriverCarBindingRelationship getDriverCarBindingRelationship(Long carId) {
        return new LambdaQueryChainWrapper<>(driverCarBindingRelationshipMapper)
                .eq(DriverCarBindingRelationship::getCarId, carId)
                .eq(DriverCarBindingRelationship::getBindState, DriverCarConstants.DRIVER_CAR_BIND)
                .one();
    }


}
