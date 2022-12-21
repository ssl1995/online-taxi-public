package com.ssl.note.controller;

import com.ssl.note.dto.*;
import com.ssl.note.service.UserService;
import com.ssl.note.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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

    @PostMapping("/driver-user-word-status")
    public ResponseResult<String> changeWorkStatus(@RequestBody DriverUserWorkStatus driverUserWorkStatus) {
        return userService.changeWorkStatus(driverUserWorkStatus);
    }

    @GetMapping("/driver-car-binding-relationship")
    public ResponseResult<DriverCarBindingRelationship> getDriverCarBindingRelationship(HttpServletRequest request) {
        // 从Token里获取手机号
        String authorization = request.getHeader("Authorization");
        TokenResult tokenResult = JwtUtils.checkToken(authorization);

        return userService.getDriverCarBindingRelationship(tokenResult.getPhone());
    }
}
