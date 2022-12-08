package com.ssl.note.service;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.mapper.DriverUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/08 22:45
 * @Describe:
 */
@Service
public class CityDriverUserService {

    @Autowired
    private DriverUserMapper driverUserMapper;

    public ResponseResult<Boolean> isAvailableDriver(String cityCode) {
        return ResponseResult.success(driverUserMapper.selectDriverUserCountByCityCode(cityCode) > 0);
    }
}
