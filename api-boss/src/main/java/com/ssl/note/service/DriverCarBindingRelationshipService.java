package com.ssl.note.service;

import com.ssl.note.dto.DriverCarBindingRelationship;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.DriverUserClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/02 16:51
 * @Describe:
 */
@Service
public class DriverCarBindingRelationshipService {

    @Resource
    private DriverUserClient driverUserClient;


    public ResponseResult<String> bind(DriverCarBindingRelationship driverCarBindingRelationship) {
        return driverUserClient.bind(driverCarBindingRelationship);
    }

    public ResponseResult<String> unbind(DriverCarBindingRelationship driverCarBindingRelationship) {
        return driverUserClient.unbind(driverCarBindingRelationship);
    }
}
