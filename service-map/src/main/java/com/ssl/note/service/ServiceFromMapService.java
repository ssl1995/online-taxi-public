package com.ssl.note.service;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.ServiceFromMapClient;
import com.ssl.note.response.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/03 21:36
 * @Describe:
 */
@Service
public class ServiceFromMapService {

    @Autowired
    private ServiceFromMapClient serviceFromMapClient;

    public ResponseResult<ServiceResponse> addService(String name) {
        return serviceFromMapClient.addService(name);
    }


}
