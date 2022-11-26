package com.ssl.note.controller;

import com.ssl.note.dto.DriverUser;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.service.DriverUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


    @GetMapping("/test")
    public DriverUser test(Long id) {
        return driverUserService.getTest(id);
    }

}
