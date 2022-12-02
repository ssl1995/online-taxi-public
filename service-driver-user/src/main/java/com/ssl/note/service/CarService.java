package com.ssl.note.service;

import com.ssl.note.dto.Car;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.mapper.CarMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/28 22:45
 * @Describe:
 */
@Service
public class CarService {

    @Autowired
    private CarMapper carMapper;

    public ResponseResult<String> saveCar(Car car) {
        LocalDateTime now = LocalDateTime.now();
        car.setGmtCreate(now);
        car.setGmtModified(now);
        carMapper.insert(car);
        return ResponseResult.success("");
    }

}
