package com.ssl.note.controller;

import com.ssl.note.dto.PassengerUser;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.request.VerificationCodeDTO;
import com.ssl.note.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/18 08:12
 * @Describe:
 */
@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 登录或注册用户
     */
    @PostMapping("/user")
    public ResponseResult loginOrRegUser(@RequestBody VerificationCodeDTO verificationCodeDTO) {
        String passengerPhone = verificationCodeDTO.getPassengerPhone();
        userService.loginOrRegUser(passengerPhone);
        return ResponseResult.success();
    }

    /**
     * 获取用户信息
     * openFeign的问题，如果是Body去传递，回将请求方式自动改为POST
     */
    @GetMapping("/user/{phone}")
    public ResponseResult<PassengerUser> getUserByPhone(@PathVariable("phone") String passengerPhone) {
        log.info("调用【getUserByPhone，参数={}】", passengerPhone);
        return userService.getUser(passengerPhone);
    }
}
