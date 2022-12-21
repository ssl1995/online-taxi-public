package com.ssl.note.service;

import com.ssl.note.dto.DriverUser;
import com.ssl.note.dto.DriverUserWorkStatus;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.ServiceDriverUserClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/26 16:44
 * @Describe:
 */
@Service
public class UserService {

    @Resource
    private ServiceDriverUserClient driverUserClient;

    public ResponseResult<String> addDriverUser(DriverUser driverUser) {
        return driverUserClient.addUser(driverUser);
    }

    public ResponseResult<String> updateDriverUser(DriverUser driverUser) {
        return driverUserClient.updateUser(driverUser);
    }

    public ResponseResult<String> changeWorkStatus(DriverUserWorkStatus driverUserWorkStatus) {
        return driverUserClient.changeWorkStatus(driverUserWorkStatus);
    }
}
