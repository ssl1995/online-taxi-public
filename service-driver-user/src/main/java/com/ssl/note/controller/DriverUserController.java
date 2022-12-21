package com.ssl.note.controller;

import com.ssl.note.constant.DriverCarConstants;
import com.ssl.note.dto.DriverCarBindingRelationship;
import com.ssl.note.dto.DriverUser;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.response.DriverUserExistsResponse;
import com.ssl.note.response.OrderDriverResponse;
import com.ssl.note.service.DriverUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/25 21:30
 * @Describe:
 */
@RestController
public class DriverUserController {

    @Autowired
    private DriverUserService driverUserService;

    @PostMapping("/user")
    public ResponseResult<String> addUser(@RequestBody DriverUser driverUser) {
        return driverUserService.addUser(driverUser);
    }

    @PutMapping("/user")
    public ResponseResult<String> updateUser(@RequestBody DriverUser driverUser) {
        return driverUserService.updateUser(driverUser);
    }


    @GetMapping("/check-driver/{driverPhone}")
    public ResponseResult<DriverUserExistsResponse> getUser(@PathVariable("driverPhone") String driverPhone) {
        ResponseResult<DriverUser> driverUserResp = driverUserService.getDriverUserByPhone(driverPhone);
        DriverUserExistsResponse resp = new DriverUserExistsResponse();

        if (Objects.isNull(driverUserResp)
                || Objects.isNull(driverUserResp.getData())
                || Objects.isNull(driverUserResp.getData().getDriverPhone())) {
            resp.setIfExists(DriverCarConstants.DRIVER_NOT_EXISTS);
            resp.setDriverPhone(driverPhone);
            return ResponseResult.success(resp);
        }

        resp.setIfExists(DriverCarConstants.DRIVER_EXISTS);
        resp.setDriverPhone(driverUserResp.getData().getDriverPhone());
        return ResponseResult.success(resp);
    }

    @GetMapping("/get-available-driver/{carId}")
    public ResponseResult<OrderDriverResponse> getAvailableDriverByCarId(@PathVariable("carId") Long carId) {
        return driverUserService.getAvailableDriverByCarId(carId);
    }

    @GetMapping("/driver-car-binding-relationship")
    public ResponseResult<DriverCarBindingRelationship> getDriverCarBindingRelationship(@RequestParam("driverPhone") String driverPhone) {
        return driverUserService.getDriverCarBindingRelationship(driverPhone);
    }

}
