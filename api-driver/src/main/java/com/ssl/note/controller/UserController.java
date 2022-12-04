package com.ssl.note.controller;

import com.ssl.note.dto.DriverUser;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/26 16:42
 * @Describe:
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PutMapping("/user")
    public ResponseResult<String> updateDriverUser(@RequestBody DriverUser driverUser) {
        return userService.updateDriverUser(driverUser);
    }
}
