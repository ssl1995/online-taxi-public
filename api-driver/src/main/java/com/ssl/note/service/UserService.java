package com.ssl.note.service;

import com.ssl.note.dto.DriverUser;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.DriverUserClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/26 16:44
 * @Describe:
 */
@Service
public class UserService {

    @Resource
    private DriverUserClient driverUserClient;

    public ResponseResult<String> addDriverUser(@RequestBody DriverUser driverUser) {
        return driverUserClient.addUser(driverUser);
    }

    public ResponseResult<String> updateDriverUser(@RequestBody DriverUser driverUser) {
        return driverUserClient.updateUser(driverUser);
    }
}
