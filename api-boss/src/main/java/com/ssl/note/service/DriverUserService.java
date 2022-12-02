package com.ssl.note.service;

import com.ssl.note.dto.Car;
import com.ssl.note.dto.DriverCarBindingRelationship;
import com.ssl.note.dto.DriverUser;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.DriverUserClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/26 16:44
 * @Describe:
 */
@Service
public class DriverUserService {

    @Resource
    private DriverUserClient driverUserClient;

    public ResponseResult<String> addDriverUser(DriverUser driverUser) {
        return driverUserClient.addUser(driverUser);
    }

    public ResponseResult<String> updateDriverUser(DriverUser driverUser) {
        return driverUserClient.updateUser(driverUser);
    }

    public ResponseResult<String> addCar(Car car) {
        return driverUserClient.saveCar(car);
    }

    public ResponseResult<String> bind(DriverCarBindingRelationship driverCarBindingRelationship) {
        return driverUserClient.bind(driverCarBindingRelationship);
    }

    public ResponseResult<String> unbind(DriverCarBindingRelationship driverCarBindingRelationship) {
        return driverUserClient.unbind(driverCarBindingRelationship);
    }
}
