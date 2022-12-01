package com.ssl.note.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.constant.DriverCarConstants;
import com.ssl.note.dto.DriverCarBindingRelationship;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.mapper.DriverCarBindingRelationshipMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/01 18:08
 * @Describe:
 */
@Service
public class DriverCarBindingRelationshipService {

    @Autowired
    private DriverCarBindingRelationshipMapper driverCarBindingRelationshipMapper;

    public ResponseResult<String> bind(DriverCarBindingRelationship driverCarBindingRelationship) {
        // 1.检查车辆司机是否存在
        QueryWrapper<DriverCarBindingRelationship> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("driver_id", driverCarBindingRelationship.getDriverId());
        queryWrapper.eq("car_id", driverCarBindingRelationship.getCarId());
        queryWrapper.eq("bind_state", DriverCarConstants.DRIVER_CAR_BIND);

        Integer count = driverCarBindingRelationshipMapper.selectCount(queryWrapper);
        if (!Objects.isNull(count) && count > 0) {
            return ResponseResult.fail(CommonStatusEnum.DRIVER_CAR_BIND_EXISTS.getCode(),
                    CommonStatusEnum.DRIVER_CAR_BIND_EXISTS.getMessage());
        }

        // 2.检查司机是否存在
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("driver_id", driverCarBindingRelationship.getDriverId());
        queryWrapper.eq("bind_state", DriverCarConstants.DRIVER_CAR_BIND);

        Integer count1 = driverCarBindingRelationshipMapper.selectCount(queryWrapper);
        if (!Objects.isNull(count1) && count1 > 0) {
            return ResponseResult.fail(CommonStatusEnum.DRIVER_BIND_EXISTS.getCode(),
                    CommonStatusEnum.DRIVER_BIND_EXISTS.getMessage());
        }

        // 3.检查车辆是否存在
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("car_id", driverCarBindingRelationship.getCarId());
        queryWrapper.eq("bind_state", DriverCarConstants.DRIVER_CAR_BIND);

        Integer count2 = driverCarBindingRelationshipMapper.selectCount(queryWrapper);
        if (!Objects.isNull(count2) && count2 > 0) {
            return ResponseResult.fail(CommonStatusEnum.CAR_BIND_EXISTS.getCode(),
                    CommonStatusEnum.CAR_BIND_EXISTS.getMessage());
        }


        LocalDateTime now = LocalDateTime.now();
        driverCarBindingRelationship.setBindingTime(now);
        driverCarBindingRelationship.setBindState(DriverCarConstants.DRIVER_CAR_BIND);

        driverCarBindingRelationshipMapper.insert(driverCarBindingRelationship);

        return ResponseResult.success("");
    }

    public ResponseResult<String> unbind(DriverCarBindingRelationship driverCarBindingRelationship) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("driver_id", driverCarBindingRelationship.getDriverId());
        map.put("car_id", driverCarBindingRelationship.getCarId());
        map.put("bind_state", DriverCarConstants.DRIVER_CAR_BIND);

        List<DriverCarBindingRelationship> driverCarBindingRelationshipList = driverCarBindingRelationshipMapper.selectByMap(map);
        // 检车车辆和司机关系是否存在
        if (CollectionUtils.isEmpty(driverCarBindingRelationshipList)) {
            return ResponseResult.fail(CommonStatusEnum.DRIVER_CAR_BIND_NOT_EXISTS.getCode(),
                    CommonStatusEnum.DRIVER_CAR_BIND_NOT_EXISTS.getMessage());
        }

        driverCarBindingRelationship = driverCarBindingRelationshipList.get(0);
        LocalDateTime now = LocalDateTime.now();
        driverCarBindingRelationship.setUnBindingTime(now);
        driverCarBindingRelationship.setBindState(DriverCarConstants.DRIVER_CAR_UNBIND);
        driverCarBindingRelationshipMapper.updateById(driverCarBindingRelationship);

        return ResponseResult.success("");
    }

}
