package com.ssl.note.controller;

import com.ssl.note.dto.Car;
import com.ssl.note.dto.DriverCarBindingRelationship;
import com.ssl.note.dto.DriverUser;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.service.DriverUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/26 16:42
 * @Describe:
 */
@RestController
public class DriverUserController {

    @Autowired
    private DriverUserService driverUserService;

    @PostMapping("/driver-user")
    public ResponseResult<String> addDriverUser(@RequestBody DriverUser driverUser) {
        return driverUserService.addDriverUser(driverUser);
    }

    @PutMapping("/driver-user")
    public ResponseResult<String> updateDriverUser(@RequestBody DriverUser driverUser) {
        return driverUserService.updateDriverUser(driverUser);
    }

    @PostMapping("/car")
    public ResponseResult<String> addCar(@RequestBody Car car) {
        return driverUserService.addCar(car);
    }

    @PostMapping("/dirver-car-binding-relationship/bind")
    public ResponseResult<String> bind(@RequestBody DriverCarBindingRelationship driverCarBindingRelationship) {
        return driverUserService.bind(driverCarBindingRelationship);
    }

    @PostMapping("/dirver-car-binding-relationship/unbind")
    public ResponseResult<String> unbind(@RequestBody DriverCarBindingRelationship driverCarBindingRelationship) {
        return driverUserService.unbind(driverCarBindingRelationship);
    }
}
