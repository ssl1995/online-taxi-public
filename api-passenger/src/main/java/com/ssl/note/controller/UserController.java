package com.ssl.note.controller;

import com.ssl.note.constant.HeaderConstant;
import com.ssl.note.dto.PassengerUser;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author SongShengLin
 * @date 2022/11/19 12:53
 * @description
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户信息
     * 手机号登信息可以从Token中取得，无需当做实名参数传递
     */
    @GetMapping("/users")
    public ResponseResult<PassengerUser> getUsers(HttpServletRequest request) {

        String accessToken = request.getHeader(HeaderConstant.AUTHORIZATION);

        return userService.getUserByAccessToken(accessToken);
    }
}
